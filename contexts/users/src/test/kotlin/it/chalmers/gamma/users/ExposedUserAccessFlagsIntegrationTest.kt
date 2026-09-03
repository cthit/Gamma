package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.database.DatabaseFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExposedUserAccessFlagsIntegrationTest {
    @Test
    fun `replaces flag state for a user beyond one directory page`() =
        withUserDatabase { database ->
            val accessFlags = ExposedUserAccessFlags(database)
            val bulkUserIds = insertBulkUsers(database)
            val earlyUserId = bulkUserIds.first()
            database.executeSqlScript(
                "INSERT INTO g_admin_user (user_id, created_at) VALUES ('${earlyUserId.value}', NOW())",
            )

            run {
                val userBeyondFirstPage = bulkUserIds.last()
                accessFlags.replaceAccessFlags(
                    earlyUserId,
                    UserAccessFlagKind.GDPR_TRAINED,
                    setOf(userBeyondFirstPage),
                )
                val gdprFlags = accessFlags.listAccessFlags(earlyUserId, UserAccessFlagKind.GDPR_TRAINED)

                assertTrue(gdprFlags.indexOfFirst { it.userId == userBeyondFirstPage } >= 200)
                assertEquals(
                    setOf(userBeyondFirstPage),
                    gdprFlags.filter(UserAccessFlag::enabled).mapTo(mutableSetOf(), UserAccessFlag::userId),
                )
            }
        }

    @Test
    fun `administrator responsibility can rotate in either CID order`() =
        withUserDatabase { database ->
            val accessFlags = ExposedUserAccessFlags(database)
            val earlyUserId = insertUser(database, cid = "aaaauser")
            val lateUserId = insertUser(database, cid = "zzzzuser")
            database.executeSqlScript(
                "INSERT INTO g_admin_user (user_id, created_at) VALUES ('${earlyUserId.value}', NOW())",
            )

            run {
                accessFlags.replaceAccessFlags(earlyUserId, UserAccessFlagKind.ADMINISTRATOR, setOf(lateUserId))
                assertEquals(setOf(lateUserId), accessFlags.enabledAdministrators(lateUserId))

                accessFlags.replaceAccessFlags(lateUserId, UserAccessFlagKind.ADMINISTRATOR, setOf(earlyUserId))
                assertEquals(setOf(earlyUserId), accessFlags.enabledAdministrators(earlyUserId))
            }
        }

    @Test
    fun `authorization precedes validation and the final administrator remains assigned`() =
        withUserDatabase { database ->
            val accessFlags = ExposedUserAccessFlags(database)
            val queries = UserStoreForQueries(database)

            run {
                val administratorId = checkNotNull(queries.findUser(Cid(FIXTURE_ADMINISTRATOR_CID))).id
                val ordinaryUserId = checkNotNull(queries.findUser(Cid("jhalpert"))).id
                assertFailsWith<AccessDenied> {
                    accessFlags.replaceAccessFlags(ordinaryUserId, UserAccessFlagKind.GDPR_TRAINED, emptySet())
                }
                assertFailsWith<AccessDenied> {
                    accessFlags.replaceAccessFlags(ordinaryUserId, UserAccessFlagKind.ADMINISTRATOR, emptySet())
                }
                assertFailsWith<AccessDenied> {
                    accessFlags.replaceAccessFlags(
                        ordinaryUserId,
                        UserAccessFlagKind.GDPR_TRAINED,
                        setOf(UserId.generate()),
                    )
                }
                assertFailsWith<UserConflict> {
                    accessFlags.replaceAccessFlags(administratorId, UserAccessFlagKind.ADMINISTRATOR, emptySet())
                }
                assertEquals(setOf(administratorId), accessFlags.enabledAdministrators(administratorId))
            }
        }

    private fun insertBulkUsers(database: DatabaseFactory): List<UserId> {
        val users =
            List(201) { index ->
                val cid = "bulk${index.toBase26().padStart(3, 'a')}"
                cid to UserId.generate()
            }
        database.executeSqlScript(
            users.joinToString(separator = ";\n") { (cid, userId) -> userInsert(cid, userId) },
        )
        return users.map { (_, userId) -> userId }
    }

    private fun insertUser(
        database: DatabaseFactory,
        cid: String,
    ): UserId {
        val userId = UserId.generate()
        database.executeSqlScript(userInsert(cid, userId))
        return userId
    }

    private fun userInsert(
        cid: String,
        userId: UserId,
    ): String =
        """
        INSERT INTO g_user (
            user_id, cid, password, nick, first_name, last_name, email,
            user_agreement_accepted, acceptance_year, version, locked, created_at, updated_at
        ) VALUES (
            '${userId.value}', '$cid', NULL, 'Bulk', 'Bulk', 'User',
            '$cid@example.org', NOW(), 2020, 0, FALSE, NOW(), NOW()
        )
        """.trimIndent()
}

private fun UserStore.enabledAdministrators(administratorId: UserId): Set<UserId> =
    listAccessFlags(administratorId, UserAccessFlagKind.ADMINISTRATOR)
        .filter(UserAccessFlag::enabled)
        .mapTo(mutableSetOf(), UserAccessFlag::userId)
