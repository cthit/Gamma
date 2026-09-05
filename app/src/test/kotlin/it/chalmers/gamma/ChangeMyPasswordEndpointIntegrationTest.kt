package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.RegisterUser
import it.chalmers.gamma.users.UserQueries
import it.chalmers.gamma.users.UserRegistration
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ChangeMyPasswordEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var activations: ActivationCodes

    @Autowired
    private lateinit var registration: RegisterUser

    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var deletion: UserDeletionCascade

    @Test
    fun `password form preserves rejection rendering and both update routes change the actual login`() {
        val cid = Cid("pwdstudent")
        activations.allow(cid)
        val userId =
            registration.register(
                Actor.Anonymous,
                UserRegistration(
                    database.seedActivationForTest(cid),
                    Nick("Password student"),
                    FirstName("Password"),
                    LastName("Student"),
                    AcceptanceYear.of(2021, 2026),
                    Language.EN,
                    Email("password.student@example.org"),
                    PlainTextPassword("password1337"),
                    "password1337",
                    true,
                ),
            )
        try {
            val browser = browser(uniqueAddress())
            assertEquals(302, browser.login(cid.value).status)
            val (page, csrf) = browser.csrf("/me/edit-password")
            assertEquals(200, page.status)
            val fields =
                mapOf(
                    "currentPassword" to "password1337",
                    "newPassword" to "replacement password",
                    "confirmNewPassword" to "replacement password",
                    "_csrf" to csrf,
                )
            val mismatch = browser.form("PUT", "/me/password", fields + ("confirmNewPassword" to "different"))
            assertEquals(409, mismatch.status)
            assertContains(mismatch.body, "Passwords do not match")
            val incorrect = browser.form("PUT", "/me/password", fields + ("currentPassword" to "incorrect password"))
            assertEquals(409, incorrect.status)
            assertContains(incorrect.body, "Incorrect password")
            assertEquals(0, assertNotNull(users.findUser(userId)).version)

            val changed = browser.form("PUT", "/me/password", fields)
            assertEquals(302, changed.status)
            assertEquals("/?passwordChanged=true", changed.header("Location"))
            assertEquals(1, assertNotNull(users.findUser(userId)).version)
            val oldPassword = browser(uniqueAddress()).login(cid.value)
            assertContains(assertNotNull(oldPassword.header("Location")), "error")
            val renewed = browser(uniqueAddress())
            assertEquals(302, renewed.login(cid.value, "replacement password").status)
            assertEquals(200, renewed.get("/me").status)

            val (_, renewedCsrf) = renewed.csrf("/me/edit-password")
            val alias =
                renewed.form(
                    "PUT",
                    "/me/edit-password",
                    fields +
                        mapOf(
                            "currentPassword" to "replacement password",
                            "newPassword" to "second replacement password",
                            "confirmNewPassword" to "second replacement password",
                            "_csrf" to renewedCsrf,
                        ),
                )
            assertEquals(302, alias.status)
            assertEquals(2, assertNotNull(users.findUser(userId)).version)
            val finalLogin = browser(uniqueAddress())
            assertEquals(302, finalLogin.login(cid.value, "second replacement password").status)
            assertEquals(200, finalLogin.get("/me").status)
        } finally {
            deletion.delete(
                AccountDeletion.Administrator(
                    deletionTestAdministrator,
                    userId,
                ),
            )
        }
    }
}
