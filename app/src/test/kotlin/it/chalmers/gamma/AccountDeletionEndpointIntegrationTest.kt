package it.chalmers.gamma

import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.UserQueries
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AccountDeletionEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var deletion: UserDeletionCascade

    @Test
    fun `administrator and personal account deletion preserve their HTTP and session contracts`() {
        val administrator = browser(uniqueAddress())
        assertEquals(302, administrator.login().status)
        val (_, adminCsrf) = administrator.csrf("/users/create")
        val cids = listOf("selfdelete", "admdelete")
        try {
            for (cid in cids) {
                val created =
                    administrator.form(
                        "POST",
                        "/users/create",
                        mapOf(
                            "cid" to cid,
                            "nick" to "Deletion student",
                            "firstName" to "Deletion",
                            "lastName" to "Student",
                            "acceptanceYear" to "2021",
                            "language" to "EN",
                            "email" to "$cid@example.org",
                            "password" to "password1337",
                            "_csrf" to adminCsrf,
                        ),
                    )
                assertEquals(302, created.status)
            }
            val personal = browser(uniqueAddress())
            assertEquals(302, personal.login("selfdelete").status)
            val (_, personalCsrf) = personal.csrf("/delete-your-account")
            val other = assertNotNull(users.findUser(Cid("admdelete")))
            assertEquals(
                403,
                personal.form("DELETE", "/users/${other.id.value}", mapOf("_csrf" to personalCsrf)).status,
            )
            val incorrect =
                personal.form(
                    "DELETE",
                    "/delete-your-account",
                    mapOf("password" to "an incorrect password", "_csrf" to personalCsrf),
                )
            assertEquals(409, incorrect.status)
            assertContains(incorrect.body, "Incorrect password")
            assertNotNull(users.findUser(Cid("selfdelete")))
            val deleted =
                personal.form(
                    "DELETE",
                    "/delete-your-account",
                    mapOf("password" to "password1337", "_csrf" to personalCsrf),
                )
            assertEquals(302, deleted.status)
            assertEquals("/login?deleted", deleted.header("Location"))
            assertNull(users.findUser(Cid("selfdelete")))
            assertEquals(302, personal.get("/me").status)

            val otherSession = browser(uniqueAddress())
            assertEquals(302, otherSession.login("admdelete").status)
            val removed = administrator.form("DELETE", "/users/${other.id.value}", mapOf("_csrf" to adminCsrf))
            assertEquals(302, removed.status)
            assertEquals("/users", removed.header("Location"))
            assertNull(users.findUser(other.id))
            assertEquals(302, otherSession.get("/me").status)
        } finally {
            for (cid in cids) {
                users
                    .findUser(
                        Cid(cid),
                    )?.let { deletion.delete(AccountDeletion.Administrator(deletionTestAdministrator, it.id)) }
            }
        }
    }
}
