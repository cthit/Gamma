package it.chalmers.gamma.users

import it.chalmers.gamma.users.PasswordResetToken
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ActivationCodesIntegrationTest {
    @Test
    fun `activation code is issued for an allowed cid and consumed with registration`() =
        withUserDatabase(loadRegressionFixture = false) { database ->
            val activationCodes = ActivationCodes(database) { "a".repeat(72) }
            val users = UserStore(database, AlwaysMatchingPasswordHasher)
            run {
                val cid = Cid("student")
                activationCodes.allow(cid)
                val token = activationCodes.create(cid)
                assertEquals(cid, activationCodes.findCid(token))

                val claim = assertNotNull(activationCodes.claim(token))
                val registration = users.prepareRegistration(newUser(cid))
                val userId = users.createActivatedUser(registration, claim)

                assertEquals(userId, users.findUser(cid)?.id)
                assertNull(activationCodes.findCid(token))
                assertFalse(activationCodes.isAllowed(cid))
            }
        }

    @Test
    fun `password reset belongs to one user and is consumed with the password update`() =
        withUserDatabase { database ->
            val users = UserStore(database, AlwaysMatchingPasswordHasher)
            val passwordResets = PasswordResets(database) { "r".repeat(72) }
            run {
                val user = assertNotNull(users.findUser(Cid(FIXTURE_ADMINISTRATOR_CID)))
                val token = passwordResets.create(user.id)
                assertEquals(user.id, passwordResets.findUser(token))

                val claim = assertNotNull(passwordResets.claim(token))
                val change = users.preparePasswordChange(user.id, PlainTextPassword("a replacement password"))
                users.persistClaimedPasswordChange(change, claim)

                assertNull(passwordResets.findUser(token))
                assertTrue(users.checkPassword(user.id, PlainTextPassword("a replacement password")))
            }
        }

    @Test
    fun `unknown reset token is not resolved`() =
        withUserDatabase { database ->
            run {
                assertNull(PasswordResets(database).findUser(PasswordResetToken("x".repeat(32))))
            }
        }
}

private fun newUser(cid: Cid) =
    NewUser(
        cid = cid,
        nick = Nick("Student"),
        firstName = FirstName("Student"),
        lastName = LastName("User"),
        acceptanceYear = AcceptanceYear.of(2020, currentYear = 2026),
        language = Language.EN,
        email = Email("student@example.org"),
        password = PlainTextPassword("correct horse battery staple"),
    )
