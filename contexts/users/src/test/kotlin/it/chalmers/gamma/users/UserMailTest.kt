package it.chalmers.gamma.users

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxy
import ch.qos.logback.core.read.ListAppender
import it.chalmers.gamma.platform.notifications.MailMessage
import it.chalmers.gamma.platform.notifications.OutboundMail
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.RegistrationToken
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class UserMailTest {
    @Test
    fun `activation mail contains the cid recipient and public registration url`() =
        run {
            val deliveredMail = RecordingOutboundMail()
            val lifecycleMail = GotifyUserMail(deliveredMail, "https://gamma.example/")
            val token = RegistrationToken("r".repeat(32))

            lifecycleMail.sendActivation(Cid("mscott"), token, sourceAddress = null)

            assertEquals(
                MailMessage(
                    to = "mscott@chalmers.se",
                    subject = "Gamma activation url",
                    body =
                        "Follow the link to finish up creating your account.\n" +
                            "The link is valid for 15 minutes.\n" +
                            "https://gamma.example/register?token=${token.value}\n",
                ),
                deliveredMail.message,
            )
        }

    @Test
    fun `password recovery mail contains the account recipient and public recovery url`() =
        run {
            val deliveredMail = RecordingOutboundMail()
            val lifecycleMail = GotifyUserMail(deliveredMail, "https://gamma.example/")
            val email = Email("private@example.org")
            val token = PasswordResetToken("p".repeat(32))

            lifecycleMail.sendPasswordReset(email, token, sourceAddress = null)

            assertEquals(
                MailMessage(
                    to = email.value,
                    subject = "Password reset for Account at IT division of Chalmers",
                    body =
                        "A password reset have been requested for this account, if you have not " +
                            "requested this mail, feel free to ignore it.\n" +
                            "The link is valid for 15 minutes. Click here to reset password:\n" +
                            "https://gamma.example/forgot-password/finalize?token=${token.value}\n",
                ),
                deliveredMail.message,
            )
        }

    @Test
    fun `logs activation delivery failure without recipient or token`() {
        val failure = IllegalStateException("delivery unavailable")
        val events = ListAppender<ILoggingEvent>().apply { start() }
        val logger = LoggerFactory.getLogger(GotifyUserMail::class.java) as Logger
        logger.addAppender(events)

        try {
            val delivery = GotifyUserMail(FailingMail(failure), "https://gamma.example")
            val thrown =
                assertFailsWith<IllegalStateException> {
                    run {
                        delivery.sendActivation(
                            Cid("student"),
                            RegistrationToken("secret-token-that-is-never-logged-123"),
                            "192.0.2.10",
                        )
                    }
                }

            assertSame(failure, thrown)
            val event = events.list.single()
            assertEquals("Activation mail could not be delivered", event.formattedMessage)
            assertSame(failure, (event.throwableProxy as ThrowableProxy).throwable)
        } finally {
            logger.detachAppender(events)
            events.stop()
        }
    }
}

private class RecordingOutboundMail : OutboundMail {
    lateinit var message: MailMessage
        private set

    override fun send(message: MailMessage) {
        this.message = message
    }
}

private class FailingMail(
    private val failure: RuntimeException,
) : OutboundMail {
    override fun send(message: MailMessage): Nothing = throw failure
}
