package it.chalmers.gamma.users

import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.RegistrationToken

/** Sends activation and password-reset messages outside the users context. */
interface UserMail {
    fun sendActivation(
        cid: Cid,
        token: RegistrationToken,
        sourceAddress: String?,
    )

    fun sendPasswordReset(
        email: Email,
        token: PasswordResetToken,
        sourceAddress: String?,
    )
}
