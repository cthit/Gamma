package it.chalmers.gamma

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.ActivationCodeAdministration
import it.chalmers.gamma.users.ActivationCodes
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.UserQueries
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RegistrationEndpointIntegrationTest : SpringApplicationTest() {
    @Autowired
    private lateinit var database: DatabaseFactory

    @Autowired
    private lateinit var activationAdministration: ActivationCodeAdministration

    @Autowired
    private lateinit var activations: ActivationCodes

    @Autowired
    private lateinit var users: UserQueries

    @Autowired
    private lateinit var deletion: UserDeletionCascade

    @Test
    fun `registration form binds the token identity consumes it once and creates a usable login`() {
        val cid = Cid("regstudent")
        activations.allow(cid)
        val token = database.seedActivationForTest(cid)
        try {
            val browser = browser(uniqueAddress())
            val path = "/register?token=${token.value}"
            val (page, csrf) = browser.csrf(path)
            assertEquals(200, page.status)
            assertContains(page.body, cid.value)
            val fields =
                mapOf(
                    "token" to token.value,
                    "cid" to "mscott",
                    "nick" to "Registered student",
                    "firstName" to "Registered",
                    "lastName" to "Student",
                    "acceptanceYear" to "2021",
                    "language" to "EN",
                    "email" to "REGISTERED.STUDENT@EXAMPLE.ORG",
                    "password" to "password1337",
                    "confirmPassword" to "password1337",
                    "acceptUserAgreement" to "true",
                    "_csrf" to csrf,
                )
            assertEquals(409, browser.form("POST", "/register", fields - "acceptUserAgreement").status)
            assertEquals(cid, activations.findCid(token))
            val response = browser.form("POST", "/register", fields)
            assertEquals(302, response.status)
            assertEquals("/login?account-created", response.header("Location"))
            val created = assertNotNull(users.findUser(cid))
            assertEquals("registered.student@example.org", created.email.value)
            assertEquals("Registered student", created.nick.value)
            assertEquals(false, activations.allowedCids().contains(cid))
            assertNull(activations.findCid(token))
            assertEquals(409, browser.form("POST", "/register", fields).status)
            assertEquals(404, browser.get(path).status)
            assertEquals(302, browser.login(cid.value).status)
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
