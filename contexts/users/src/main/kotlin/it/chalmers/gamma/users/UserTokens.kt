package it.chalmers.gamma.users

import java.time.Instant

@JvmInline
value class RegistrationToken(
    val value: String,
) {
    init {
        require(value.length in 32..100) { "Registration token has an invalid length" }
    }

    override fun toString(): String = "<value redacted>"
}

@JvmInline
value class PasswordResetToken(
    val value: String,
) {
    init {
        require(value.length in 32..100) { "Password reset token has an invalid length" }
    }

    override fun toString(): String = "<value redacted>"
}

data class PendingActivation(
    val cid: Cid,
    val createdAt: Instant,
) {
    override fun toString(): String = "PendingActivation(cid=<redacted>, createdAt=$createdAt)"
}
