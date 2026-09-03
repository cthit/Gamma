package it.chalmers.gamma.users

import it.chalmers.gamma.platform.notifications.MailMessage
import it.chalmers.gamma.platform.notifications.OutboundMail
import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.RegistrationToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

class GotifyUserMail(
    private val mail: OutboundMail,
    private val publicBaseUrl: String,
) : UserMail {
    override fun sendActivation(
        cid: Cid,
        token: RegistrationToken,
        sourceAddress: String?,
    ) {
        send(
            MailMessage(
                to = "${cid.value}@chalmers.se",
                subject = "Gamma activation url",
                body =
                    "Follow the link to finish up creating your account.\n" +
                        "The link is valid for 15 minutes.\n" +
                        "${publicBaseUrl.trimEnd('/')}/register?token=${token.value}\n",
                sourceAddress = sourceAddress,
            ),
            failureMessage = "Activation mail could not be delivered",
        )
    }

    override fun sendPasswordReset(
        email: Email,
        token: PasswordResetToken,
        sourceAddress: String?,
    ) {
        send(
            MailMessage(
                to = email.value,
                subject = "Password reset for Account at IT division of Chalmers",
                body =
                    "A password reset have been requested for this account, if you have not " +
                        "requested this mail, feel free to ignore it.\n" +
                        "The link is valid for 15 minutes. Click here to reset password:\n" +
                        "${publicBaseUrl.trimEnd('/')}/forgot-password/finalize?token=${token.value}\n",
                sourceAddress = sourceAddress,
            ),
            failureMessage = "Password reset mail could not be delivered",
        )
    }

    private fun send(
        message: MailMessage,
        failureMessage: String,
    ) {
        try {
            mail.send(message)
        } catch (failure: IOException) {
            logger.error(failureMessage, failure)
            throw failure
        } catch (failure: IllegalStateException) {
            logger.error(failureMessage, failure)
            throw failure
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(GotifyUserMail::class.java)
    }
}
