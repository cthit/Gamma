package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserCommandsIntegrationTest {
    @Test
    fun `database access retains one shared lifecycle and bootstrap capability`() =
        withUserDatabase { database ->
            val access =
                createUserDatabaseAccess(
                    database,
                    AlwaysMatchingPasswordHasher,
                )

            assertTrue(access.lifecycleCredentials === access.administratorBootstrap)
        }

    @Test
    fun `reads and updates nullable identity values`() =
        withUserDatabase { database ->
            val userId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
            database.executeSqlScript(
                """
                UPDATE g_user
                SET password = NULL, language = NULL, version = NULL, locked = NULL
                WHERE user_id = '${userId.value}'
                """.trimIndent(),
            )
            val queries = UserStoreForQueries(database)
            val commands = persistence(database).commands

            run {
                val storedUser = assertNotNull(queries.findUser(userId))
                assertNull(storedUser.language)
                assertEquals(0, storedUser.version)
                assertFalse(storedUser.locked)
                assertFalse(commands.checkPassword(userId, PlainTextPassword("password1337")))

                commands.updateUser(storedUser.copy(nick = Nick("Updated row")))
                val updated = assertNotNull(queries.findUser(userId))
                assertEquals("Updated row", updated.nick.value)
                assertEquals(1, updated.version)
            }
        }

    @Test
    fun `creates and authenticates an activated identity`() =
        withUserDatabase { database ->
            val queries = UserStoreForQueries(database)
            val persistence = persistence(database)
            val commands = persistence.commands
            val password = PlainTextPassword("correct horse battery staple")

            run {
                val userId = persistence.lifecycle.createActivatedTestUser(database, testUser(password))

                assertEquals(0, assertNotNull(queries.findUser(userId)).version)
                assertTrue(commands.checkPassword(userId, password))
                assertFalse(commands.checkPassword(userId, PlainTextPassword("definitely not the password")))
            }
        }

    @Test
    fun `profile updates advance the version and reject a stale profile`() =
        withUserDatabase { database ->
            val queries = UserStoreForQueries(database)
            val persistence = persistence(database)
            val commands = persistence.commands

            run {
                val userId =
                    persistence.lifecycle.createActivatedTestUser(
                        database,
                        testUser(PlainTextPassword("correct horse battery staple")),
                    )
                val edited = assertNotNull(queries.findUser(userId)).copy(nick = Nick("Edited"))

                commands.updateUser(edited)

                assertEquals(edited.version + 1, queries.findUser(userId)?.version)
                assertEquals("Edited", queries.findUser(userId)?.nick?.value)
                assertFailsWith<UserConflict> { commands.updateUser(edited) }
            }
        }

    @Test
    fun `authenticated password replacement invalidates the previous password`() =
        withUserDatabase { database ->
            val persistence = persistence(database)
            val commands = persistence.commands
            val initialPassword = PlainTextPassword("correct horse battery staple")
            val replacement = PlainTextPassword("new correct horse password")

            run {
                val userId =
                    persistence.lifecycle.createActivatedTestUser(database, testUser(initialPassword))

                assertTrue(commands.changePassword(userId, initialPassword, replacement))
                assertFalse(commands.checkPassword(userId, initialPassword))
                assertTrue(commands.checkPassword(userId, replacement))
            }
        }

    @Test
    fun `administrator creation reports a duplicate CID as a conflict`() =
        withUserDatabase { database ->
            val persistence = persistence(database)
            val commands = persistence.commands
            val user = testUser(PlainTextPassword("correct horse battery staple"))

            run {
                persistence.lifecycle.createActivatedTestUser(database, user)

                assertFailsWith<UserConflict> {
                    commands.createUserAsAdministrator(
                        FIXTURE_ADMINISTRATOR_ID,
                        user.copy(nick = Nick("Duplicate"), email = Email("different@example.org")),
                    )
                }
            }
        }

    @Test
    fun `deleted identity cannot change its password`() =
        withUserDatabase { database ->
            val queries = UserStoreForQueries(database)
            val persistence = persistence(database)
            val commands = persistence.commands
            val password = PlainTextPassword("correct horse battery staple")

            run {
                val userId = persistence.lifecycle.createActivatedTestUser(database, testUser(password))
                val operationId = UserAvatarOperationId(UUID.fromString("72000000-0000-0000-0000-000000000004"))
                val avatar = StoredUserAvatar("${operationId.value}.png")
                commands.replaceAvatar(userId, operationId, avatar)

                assertEquals(avatar.uri, commands.deleteUser(userId))

                assertNull(queries.findUser(userId))
                assertFalse(
                    commands.changePassword(
                        userId,
                        password,
                        PlainTextPassword("another replacement password"),
                    ),
                )
            }
        }

    @Test
    fun `final administrator cannot be deleted`() =
        withUserDatabase { database ->
            val users = persistence(database).commands

            run {
                val administrator = checkNotNull(users.findUser(Cid(FIXTURE_ADMINISTRATOR_CID)))

                assertFailsWith<UserConflict> { users.deleteUser(administrator.id) }
                assertEquals(administrator, users.findUser(administrator.id))
            }
        }

    private fun persistence(database: DatabaseFactory) =
        identityPersistenceTestAdapters(database, BcryptPasswordHasher(cost = 10))

    private fun testUser(password: PlainTextPassword) =
        NewUser(
            cid = Cid("testuser"),
            nick = Nick("Regression"),
            firstName = FirstName("Test"),
            lastName = LastName("User"),
            acceptanceYear = AcceptanceYear.of(2020, currentYear = 2026),
            language = Language.EN,
            email = Email("testuser@example.org"),
            password = password,
        )
}
