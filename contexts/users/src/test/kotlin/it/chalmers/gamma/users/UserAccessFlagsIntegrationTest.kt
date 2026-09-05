package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import java.sql.SQLException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UserAccessFlagsIntegrationTest {
    @Test
    fun `replaces flag state for a user beyond one directory page`() =
        withUserDatabase { database ->
            val accessFlags = UserAccessFlags(database)
            val bulkUserIds = insertBulkUsers(database)
            val earlyUserId = bulkUserIds.first()
            database.executeSqlScript(
                "INSERT INTO g_admin_user (user_id, created_at) VALUES ('${earlyUserId.value}', NOW())",
            )

            run {
                val userBeyondFirstPage = bulkUserIds.last()
                accessFlags.replace(
                    earlyUserId.accessActor(),
                    UserAccessFlagKind.GDPR_TRAINED,
                    setOf(userBeyondFirstPage),
                )
                val gdprFlags = accessFlags.list(earlyUserId.accessActor(), UserAccessFlagKind.GDPR_TRAINED)

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
            val accessFlags = UserAccessFlags(database)
            val earlyUserId = insertUser(database, cid = "aaaauser")
            val lateUserId = insertUser(database, cid = "zzzzuser")
            database.executeSqlScript(
                "INSERT INTO g_admin_user (user_id, created_at) VALUES ('${earlyUserId.value}', NOW())",
            )

            run {
                accessFlags.replace(earlyUserId.accessActor(), UserAccessFlagKind.ADMINISTRATOR, setOf(lateUserId))
                assertEquals(setOf(lateUserId), accessFlags.enabledAdministrators(lateUserId))

                accessFlags.replace(lateUserId.accessActor(), UserAccessFlagKind.ADMINISTRATOR, setOf(earlyUserId))
                assertEquals(setOf(earlyUserId), accessFlags.enabledAdministrators(earlyUserId))
            }
        }

    @Test
    fun `authorization precedes validation and the final administrator remains assigned`() =
        withUserDatabase { database ->
            val accessFlags = UserAccessFlags(database)
            val queries = UserQueries(database)

            run {
                val administratorId = checkNotNull(queries.findUser(Cid(FIXTURE_ADMINISTRATOR_CID))).id
                val ordinaryUserId = checkNotNull(queries.findUser(Cid("jhalpert"))).id
                assertFailsWith<AccessDenied> {
                    accessFlags.replace(ordinaryUserId.accessActor(), UserAccessFlagKind.GDPR_TRAINED, emptySet())
                }
                assertFailsWith<AccessDenied> {
                    accessFlags.replace(ordinaryUserId.accessActor(), UserAccessFlagKind.ADMINISTRATOR, emptySet())
                }
                assertFailsWith<AccessDenied> {
                    accessFlags.replace(
                        ordinaryUserId.accessActor(),
                        UserAccessFlagKind.GDPR_TRAINED,
                        setOf(UserId.generate()),
                    )
                }
                assertFailsWith<UserConflict> {
                    accessFlags.replace(administratorId.accessActor(), UserAccessFlagKind.ADMINISTRATOR, emptySet())
                }
                assertEquals(setOf(administratorId), accessFlags.enabledAdministrators(administratorId))
            }
        }

    @Test
    fun `failed demotion rolls back the preceding promotion`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val administrator = checkNotNull(queries.findUser(Cid("mscott"))).id
            val replacement = checkNotNull(queries.findUser(Cid("jhalpert"))).id
            val flags = UserAccessFlags(database)
            val previous = flags.list(administrator.accessActor(), UserAccessFlagKind.ADMINISTRATOR)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_test_demotion() RETURNS trigger AS ${'$'}${'$'}
                BEGIN
                    RAISE EXCEPTION 'injected demotion failure';
                END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_test_demotion BEFORE DELETE ON g_admin_user
                FOR EACH ROW EXECUTE FUNCTION reject_test_demotion();
                """.trimIndent(),
            )
            assertFailsWith<SQLException> {
                flags.replace(administrator.accessActor(), UserAccessFlagKind.ADMINISTRATOR, setOf(replacement))
            }
            assertEquals(previous, flags.list(administrator.accessActor(), UserAccessFlagKind.ADMINISTRATOR))
        }

    @Test
    fun `competing demotions retain one administrator and deny the demoted caller`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val administrator = checkNotNull(queries.findUser(Cid("mscott"))).id
            val other = checkNotNull(queries.findUser(Cid("jhalpert"))).id
            val flags = UserAccessFlags(database)
            flags.replace(administrator.accessActor(), UserAccessFlagKind.ADMINISTRATOR, setOf(administrator, other))
            val start = CountDownLatch(1)
            Executors.newFixedThreadPool(2).use { workers ->
                val attempts =
                    listOf(administrator, other).map { caller ->
                        workers.submit<UserId?> {
                            check(start.await(10, TimeUnit.SECONDS))
                            try {
                                flags.replace(caller.accessActor(), UserAccessFlagKind.ADMINISTRATOR, setOf(caller))
                                caller
                            } catch (_: AccessDenied) {
                                null
                            }
                        }
                    }
                start.countDown()
                val winners = attempts.mapNotNull { it.get(10, TimeUnit.SECONDS) }
                assertEquals(1, winners.size)
                assertEquals(winners.toSet(), flags.enabledAdministrators(winners.single()))
            }
        }

    @Test
    fun `reads require current authority and invalid selections leave both flag sets intact`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val administrator = checkNotNull(queries.findUser(Cid("mscott"))).id
            val member = checkNotNull(queries.findUser(Cid("jhalpert"))).id
            val flags = UserAccessFlags(database)
            for (kind in UserAccessFlagKind.entries) {
                val previous = flags.list(administrator.accessActor(), kind)
                assertFailsWith<AccessDenied> { flags.list(member.accessActor(), kind) }
                assertFailsWith<AccessDenied> { flags.list(Actor.Anonymous, kind) }
                assertFailsWith<AccessDenied> { flags.replace(Actor.Anonymous, kind, emptySet()) }
                assertFailsWith<UserNotFound> {
                    flags.replace(administrator.accessActor(), kind, setOf(UserId.generate()))
                }
                assertEquals(previous, flags.list(administrator.accessActor(), kind))
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

private fun UserAccessFlags.enabledAdministrators(administratorId: UserId): Set<UserId> =
    list(administratorId.accessActor(), UserAccessFlagKind.ADMINISTRATOR)
        .filter(UserAccessFlag::enabled)
        .mapTo(mutableSetOf(), UserAccessFlag::userId)

private fun UserId.accessActor() = Actor.User(ActorUserId(value), true)
