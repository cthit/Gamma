package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.database.DatabaseFactory
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserQueriesIntegrationTest {
    @Test
    fun `reads public administrative and API identity views`() =
        withUserDatabase { database ->
            val identities: UserQueries = UserQueries(database)

            run {
                val michael = assertNotNull(identities.findUser(Cid(FIXTURE_ADMINISTRATOR_CID)))
                val jim = assertNotNull(identities.findUser(Cid("jhalpert")))

                assertEquals("Boss", michael.nick.value)
                assertEquals("mscott@example.org", michael.email.value)
                assertEquals(
                    michael.id,
                    database
                        .commitTransaction(readOnly = true) {
                            identities.findDirectoryUserIn(this, michael.id)
                        }?.id,
                )
                assertEquals(michael, identities.administrativeUser(michael.id, michael.id)?.profile)
                assertTrue(checkNotNull(identities.administrativeUser(michael.id, michael.id)).gdprTrained)
                assertTrue(identities.administrativeUsers(michael.id).any { it.id == jim.id })
                assertEquals(
                    michael.id,
                    database.commitTransaction(readOnly = true) { identities.apiUserIn(this, michael.id) }?.id,
                )
                assertTrue(
                    checkNotNull(
                        database.commitTransaction(readOnly = true) {
                            identities.apiUserIn(this, michael.id)
                        },
                    ).gdprTrained,
                )
                assertTrue(
                    database.commitTransaction(readOnly = true) { identities.apiUsersIn(this) }.any {
                        it.id ==
                            jim.id
                    },
                )
                assertNull(
                    database.commitTransaction(
                        readOnly = true,
                    ) { identities.findDirectoryUserIn(this, UserId.generate()) },
                )
                assertNull(
                    database.commitTransaction(readOnly = true) { identities.apiUserIn(this, UserId.generate()) },
                )
            }
        }

    @Test
    fun `privileged identity views reject an ordinary user`() =
        withUserDatabase { database ->
            val identities: UserQueries = UserQueries(database)

            run {
                val michael = assertNotNull(identities.findUser(Cid(FIXTURE_ADMINISTRATOR_CID)))
                val jim = assertNotNull(identities.findUser(Cid("jhalpert")))

                assertFailsWith<AccessDenied> {
                    identities.directoryPage(scope = DirectoryUserScope.administrator(jim.id))
                }
                assertFailsWith<AccessDenied> { identities.administrativeUser(jim.id, michael.id) }
                assertFailsWith<AccessDenied> { identities.administrativeUsers(jim.id) }
            }
        }

    @Test
    fun `identity lookup normalizes email but directory search excludes email`() =
        withUserDatabase { database ->
            val identities: UserQueries = UserQueries(database)

            run {
                val michael = assertNotNull(identities.findUser(Cid(FIXTURE_ADMINISTRATOR_CID)))
                val jim = assertNotNull(identities.findUser(Cid("jhalpert")))
                database.commitTransaction {
                    exec("UPDATE g_user SET email = 'Jim.Halpert@Example.ORG' WHERE user_id = '${jim.id.value}'")
                }

                assertEquals(jim.id, identities.findUser(Email("jim.halpert@example.org"))?.id)
                assertTrue(identities.directoryPage("Michael Scott").users.any { it.id == michael.id })
                assertTrue(identities.directoryPage("mscott@example.org").users.isEmpty())
                assertTrue(identities.directoryPage("example.org").users.isEmpty())
            }
        }

    @Test
    fun `directory pages are ordered bounded and hide locked users from ordinary viewers`() =
        withUserDatabase { database ->
            val identities: UserQueries = UserQueries(database)
            insertBulkUsers(database, count = 210)

            run {
                val firstPage = identities.directoryPage()
                assertEquals(200, firstPage.users.size)
                val firstPageCids = firstPage.users.map { it.cid }
                assertTrue(firstPageCids.zipWithNext().all { (first, second) -> first.value < second.value })
                val firstPageCursor = assertNotNull(firstPage.nextCid)
                assertEquals(firstPageCids.last(), firstPageCursor)

                val secondPage = identities.directoryPage(afterCid = firstPageCursor)
                assertEquals(23, secondPage.users.size)
                val secondPageCids = secondPage.users.map { it.cid }
                assertTrue(secondPageCids.first().value > firstPageCursor.value)
                assertEquals(
                    firstPageCids.size + secondPageCids.size,
                    (firstPageCids + secondPageCids).distinct().size,
                )
                assertNull(secondPage.nextCid)
                assertTrue(identities.directoryPage(afterCid = secondPageCids.last()).users.isEmpty())

                database.commitTransaction { exec("UPDATE g_user SET locked = TRUE WHERE cid LIKE 'bulk%'") }
                val jim = assertNotNull(identities.findUser(Cid("jhalpert")))
                val ordinaryPage = identities.directoryPage(scope = DirectoryUserScope.visibleToUser(jim.id))
                assertTrue(ordinaryPage.users.any { it.cid.value == FIXTURE_ADMINISTRATOR_CID })
                assertTrue(ordinaryPage.users.none { it.cid.value.startsWith("bulk") })
            }
        }
}

private fun UserQueries.directoryPage(
    query: String = "",
    afterCid: Cid? = null,
    scope: DirectoryUserScope = DirectoryUserScope.administrator(FIXTURE_ADMINISTRATOR_ID),
): DirectoryUserPage = directoryUserPage(DirectoryUserPageRequest(query, afterCid, scope))

private fun insertBulkUsers(
    database: DatabaseFactory,
    count: Int,
) {
    database.executeSqlScript(
        (0 until count).joinToString(separator = ";\n") { index ->
            val cid = "bulk${index.toBase26().padStart(3, 'a')}"
            """
            INSERT INTO g_user (
                user_id, cid, password, nick, first_name, last_name, email,
                user_agreement_accepted, acceptance_year, version, locked, created_at, updated_at
            ) VALUES (
                '${UUID.randomUUID()}', '$cid', NULL, 'Bulk', 'Bulk', 'User',
                '$cid@example.org', NOW(), 2020, 0, FALSE, NOW(), NOW()
            )
            """.trimIndent()
        },
    )
}
