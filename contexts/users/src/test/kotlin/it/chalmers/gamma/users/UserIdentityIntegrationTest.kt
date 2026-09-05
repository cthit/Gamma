package it.chalmers.gamma.users

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserIdentityIntegrationTest {
    @Test
    fun `reads nullable identity values`() =
        withUserDatabase { database ->
            val userId = UserId.parse("bc605869-9a4d-46ec-8a29-d00819d4c195")
            database.executeSqlScript(
                """
                UPDATE g_user
                SET password = NULL, language = NULL, version = NULL, locked = NULL
                WHERE user_id = '${userId.value}'
                """.trimIndent(),
            )
            val queries = UserQueries(database)
            val authentication = UserAuthentication(database, BcryptPasswordHasher(cost = 10))

            run {
                val storedUser = assertNotNull(queries.findUser(userId))
                assertNull(storedUser.language)
                assertEquals(0, storedUser.version)
                assertFalse(storedUser.locked)
                assertNull(authentication.authenticate(userId, PlainTextPassword("password1337")))
            }
        }

    @Test
    fun `creates and authenticates an activated identity`() =
        withUserDatabase { database ->
            val queries = UserQueries(database)
            val persistence = RegisterUser(database, BcryptPasswordHasher(cost = 10))
            val authentication = UserAuthentication(database, BcryptPasswordHasher(cost = 10))
            val password = PlainTextPassword("correct horse battery staple")

            run {
                val userId = persistence.createActivatedTestUser(database, testUser(password))

                assertEquals(0, assertNotNull(queries.findUser(userId)).version)
                assertNotNull(authentication.authenticate(userId, password))
                assertNull(authentication.authenticate(userId, PlainTextPassword("definitely not the password")))
            }
        }

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
