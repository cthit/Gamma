package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.CreatePasswordReset
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.PasswordResets
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.RegisterUser
import it.chalmers.gamma.users.UserQueries
import it.chalmers.gamma.users.UserRegistration
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ResetPasswordEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var activations: ActivationCodes

    @Autowired
    private lateinit var registration: RegisterUser

    @Autowired
    private lateinit var resets: PasswordResets

    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var deletion: UserDeletionCascade

    @Autowired
    private lateinit var resetCreation: CreatePasswordReset

    @Test
    fun `administrator issued reset links preserve authority token consumption and the full login flow`() {
        val cid = Cid("resetuser")
        activations.allow(cid)
        val userId =
            registration.register(
                Actor.Anonymous,
                UserRegistration(
                    database.seedActivationForTest(cid),
                    Nick("Reset student"),
                    FirstName("Reset"),
                    LastName("Student"),
                    AcceptanceYear.of(2021, 2026),
                    Language.EN,
                    Email("reset.student@example.org"),
                    PlainTextPassword("password1337"),
                    "password1337",
                    true,
                ),
            )
        try {
            val otherUser = assertNotNull(users.findUser(Cid("mscott")))
            val previous = resetCreation.create(deletionTestAdministrator, userId).token
            val member = browser(uniqueAddress())
            assertEquals(302, member.login(cid.value).status)
            val (_, memberCsrf) = member.csrf("/me/edit")
            val issuancePath = "/users/${userId.value}/generate-password-link"
            assertEquals(403, member.form("POST", issuancePath, mapOf("_csrf" to memberCsrf)).status)
            assertEquals(userId, resets.findUser(previous))
            val admin = browser(uniqueAddress())
            assertEquals(302, admin.login().status)
            val (_, adminCsrf) = admin.csrf("/users/${userId.value}")
            val issued = admin.form("POST", issuancePath, mapOf("_csrf" to adminCsrf))
            assertEquals(200, issued.status)
            assertContains(issued.body, "Reset student")
            val token =
                PasswordResetToken(
                    assertNotNull(
                        Regex("""forgot-password/finalize\?token=([A-Za-z0-9]+)""").find(issued.body),
                    ).groupValues[1],
                )
            assertEquals(userId, resets.findUser(token))
            assertNull(resets.findUser(previous))
            val browser = browser(uniqueAddress())
            val path = "/forgot-password/finalize?token=${token.value}"
            val (page, csrf) = browser.csrf(path)
            assertEquals(200, page.status)
            val fields =
                mapOf(
                    "token" to token.value,
                    "userId" to otherUser.id.value.toString(),
                    "password" to "a replacement password",
                    "confirmPassword" to "a replacement password",
                    "_csrf" to csrf,
                )
            val mismatch =
                browser.form("POST", "/forgot-password/finalize", fields + ("confirmPassword" to "different"))
            assertEquals(409, mismatch.status)
            assertEquals(userId, resets.findUser(token))
            assertEquals(0, assertNotNull(users.findUser(userId)).version)

            val response = browser.form("POST", "/forgot-password/finalize", fields)
            assertEquals(302, response.status)
            assertEquals("/login?password-reset", response.header("Location"))
            assertEquals(1, assertNotNull(users.findUser(userId)).version)
            assertEquals(otherUser, users.findUser(otherUser.id))
            assertNull(resets.findUser(token))
            assertEquals(409, browser.form("POST", "/forgot-password/finalize", fields).status)
            assertEquals(404, browser.get(path).status)
            val oldLogin = browser(uniqueAddress()).login(cid.value)
            assertContains(assertNotNull(oldLogin.header("Location")), "error")
            val newLogin = browser(uniqueAddress())
            assertEquals(302, newLogin.login(cid.value, "a replacement password").status)
            assertEquals(200, newLogin.get("/me").status)
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
