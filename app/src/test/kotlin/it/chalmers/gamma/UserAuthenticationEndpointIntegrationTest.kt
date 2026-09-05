package it.chalmers.gamma

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserAuthenticationEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var deletion: UserDeletionCascade

    @Test
    fun `email login and existing sessions observe account locking and deletion`() {
        val administrator = browser(uniqueAddress())
        assertEquals(302, administrator.login().status)
        val (_, csrf) = administrator.csrf("/users/create")
        val created =
            administrator.form(
                "POST",
                "/users/create",
                mapOf(
                    "cid" to "authstudent",
                    "nick" to "Authentication student",
                    "firstName" to "Auth",
                    "lastName" to "Student",
                    "acceptanceYear" to "2021",
                    "language" to "EN",
                    "email" to "auth.student@example.org",
                    "password" to "password1337",
                    "_csrf" to csrf,
                ),
            )
        assertEquals(302, created.status)
        val userId = UserId.parse(assertNotNull(created.header("Location")).substringAfterLast('/'))
        try {
            val browser = browser(uniqueAddress())
            assertEquals(302, browser.login("AUTH.STUDENT@EXAMPLE.ORG").status)
            assertEquals(200, browser.get("/me").status)

            database.executeSqlScript("UPDATE g_user SET locked = true WHERE user_id = '${userId.value}'")
            val deniedSession = browser.get("/me")
            assertEquals(302, deniedSession.status)
            assertContains(assertNotNull(deniedSession.header("Location")), "/login")
            val deniedLogin = browser.login("authstudent")
            assertContains(assertNotNull(deniedLogin.header("Location")), "/login?error")

            database.executeSqlScript("UPDATE g_user SET locked = false WHERE user_id = '${userId.value}'")
            assertEquals(302, browser.login("authstudent").status)
            assertEquals(200, browser.get("/me").status)
            // Bypass session eviction deliberately: the next request must detect the missing row itself.
            database.executeSqlScript("DELETE FROM g_user WHERE user_id = '${userId.value}'")
            val deletedSession = browser.get("/me")
            assertEquals(302, deletedSession.status)
            assertContains(assertNotNull(deletedSession.header("Location")), "/login")
            val missingLogin = browser.login("authstudent")
            assertContains(assertNotNull(missingLogin.header("Location")), "/login?error")
        } finally {
            if (users.findUser(userId) != null) {
                deletion.delete(
                    AccountDeletion.Administrator(
                        deletionTestAdministrator,
                        userId,
                    ),
                )
            }
        }
    }
}
