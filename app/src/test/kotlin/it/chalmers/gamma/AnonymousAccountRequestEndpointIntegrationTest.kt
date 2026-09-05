package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.users.AcceptanceYear
import it.chalmers.gamma.users.ActivationCodeAdministration
import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.FirstName
import it.chalmers.gamma.users.Language
import it.chalmers.gamma.users.LastName
import it.chalmers.gamma.users.Nick
import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.PasswordResets
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.RegisterUser
import it.chalmers.gamma.users.RegistrationToken
import it.chalmers.gamma.users.UserMail
import it.chalmers.gamma.users.UserQueries
import it.chalmers.gamma.users.UserRegistration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Import(AccountRequestMailConfiguration::class)
class AnonymousAccountRequestEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var mail: CapturedAccountRequestMail

    @Autowired
    private lateinit var activationAdministration: ActivationCodeAdministration

    @Autowired
    private lateinit var activations: ActivationCodes

    @Autowired
    private lateinit var resets: PasswordResets

    @Autowired
    private lateinit var registration: RegisterUser

    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var deletion: UserDeletionCascade

    @Test
    fun `anonymous forms keep private responses and deliver usable activation and recovery links`() {
        val cid = Cid("reqstudent")
        activations.allow(cid)
        try {
            val browser = browser(uniqueAddress())
            val (activationPage, activationCsrf) = browser.csrf("/activate-cid")
            assertEquals(200, activationPage.status)
            val startedAt = System.nanoTime()
            val allowed = browser.form("POST", "/activate-cid", mapOf("cid" to cid.value, "_csrf" to activationCsrf))
            assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt) >= 3_000)
            assertEquals(302, allowed.status)
            assertEquals("/email-sent", allowed.header("Location"))
            val activationToken = assertNotNull(mail.activations.poll())
            assertEquals(cid, activations.findCid(activationToken))
            val unknown = browser.form("POST", "/activate-cid", mapOf("cid" to "unknowncid", "_csrf" to activationCsrf))
            assertEquals(allowed.status, unknown.status)
            assertEquals(allowed.header("Location"), unknown.header("Location"))
            assertNull(mail.activations.poll())

            val userId =
                registration.register(
                    Actor.Anonymous,
                    UserRegistration(
                        activationToken,
                        Nick("Request student"),
                        FirstName("Request"),
                        LastName("Student"),
                        AcceptanceYear.of(2021, 2026),
                        Language.EN,
                        Email("request.student@example.org"),
                        PlainTextPassword("password1337"),
                        "password1337",
                        true,
                    ),
                )
            val (recoveryPage, recoveryCsrf) = browser.csrf("/forgot-password")
            assertEquals(200, recoveryPage.status)
            val message = "You should have received an email with a link for resetting your password."
            for (identifier in listOf(" REQSTUDENT ", "missinguser", "not an identifier")) {
                val response =
                    browser.form(
                        "POST",
                        "/forgot-password",
                        mapOf("cidOrEmail" to identifier, "_csrf" to recoveryCsrf),
                    )
                assertEquals(200, response.status)
                assertContains(response.body, message)
            }
            val resetToken = assertNotNull(mail.resets.poll())
            assertNull(mail.resets.poll())
            assertEquals(userId, resets.findUser(resetToken))
            val (_, resetCsrf) = browser.csrf("/forgot-password/finalize?token=${resetToken.value}")
            val completed =
                browser.form(
                    "POST",
                    "/forgot-password/finalize",
                    mapOf(
                        "token" to resetToken.value,
                        "password" to "a replacement password",
                        "confirmPassword" to "a replacement password",
                        "_csrf" to resetCsrf,
                    ),
                )
            assertEquals(302, completed.status)
            assertNull(resets.findUser(resetToken))
            assertEquals(302, browser.login(cid.value, "a replacement password").status)
            assertEquals(200, browser.get("/me").status)
        } finally {
            users
                .findUser(
                    cid,
                )?.let { deletion.delete(AccountDeletion.Administrator(deletionTestAdministrator, it.id)) }
            if (activations.allowedCids().contains(cid)) {
                activationAdministration.retractCid(
                    Actor.User(ActorUserId(assertNotNull(users.findUser(Cid("mscott"))).id.value)),
                    cid,
                )
            }
        }
    }
}

@TestConfiguration(proxyBeanMethods = false)
class AccountRequestMailConfiguration {
    @Bean
    @Primary
    fun capturedAccountRequestMail() = CapturedAccountRequestMail()
}

class CapturedAccountRequestMail : UserMail {
    val activations = ConcurrentLinkedQueue<RegistrationToken>()
    val resets = ConcurrentLinkedQueue<PasswordResetToken>()

    override fun sendActivation(
        cid: Cid,
        token: RegistrationToken,
        sourceAddress: String?,
    ) {
        activations.add(token)
    }

    override fun sendPasswordReset(
        email: Email,
        token: PasswordResetToken,
        sourceAddress: String?,
    ) {
        resets.add(token)
    }
}
