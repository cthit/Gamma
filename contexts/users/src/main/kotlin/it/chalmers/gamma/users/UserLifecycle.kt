package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.RegistrationToken
import java.io.IOException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

class UserLifecycle(
    private val users: UserStore,
    private val activationCodes: ActivationCodes,
    private val passwordResets: PasswordResets,
    private val throttling: UserLifecycleThrottling,
    private val mail: UserMail,
) {
    fun requestActivation(
        actor: Actor,
        cid: Cid,
        sourceAddress: String? = null,
    ) {
        actor.requireAnonymous()
        runWithMinimumResponseTime {
            if (concealOrdinaryFailure { activationCodes.isAllowed(cid) } != true) return@runWithMinimumResponseTime
            if (concealOrdinaryFailure { throttling.allowActivation(cid) } != true) return@runWithMinimumResponseTime
            val token = concealOrdinaryFailure { activationCodes.create(cid) } ?: return@runWithMinimumResponseTime
            try {
                mail.sendActivation(cid, token, sourceAddress)
            } catch (_: IOException) {
                activationCodes.deleteIfMatches(cid, token)
            } catch (_: IllegalStateException) {
                activationCodes.deleteIfMatches(cid, token)
            }
        }
    }

    fun activationCid(
        actor: Actor,
        token: RegistrationToken,
    ): Cid? {
        actor.requireAnonymous()
        return activationCodes.findCid(token)
    }

    fun register(
        actor: Actor,
        token: RegistrationToken,
        user: NewUser,
        confirmedPassword: String,
        acceptedUserAgreement: Boolean,
    ): UserId {
        actor.requireAnonymous()
        requireNotNull(user.language) { "Language is required" }
        if (user.password.value != confirmedPassword) throw UserConflict("Password was not confirmed")
        if (!acceptedUserAgreement) throw UserConflict("User agreement must be accepted")

        val claim = activationCodes.claim(token) ?: throw UserConflict("Activation token is invalid or expired")
        if (user.cid != claim.cid) throw AccessDenied()
        val registration = users.prepareRegistration(user)
        return users.createActivatedUser(registration, claim)
    }

    fun requestPasswordReset(
        actor: Actor,
        submittedIdentifier: String,
        sourceAddress: String? = null,
    ) {
        actor.requireAnonymous()
        runWithMinimumResponseTime {
            val identifier = passwordResetIdentifier(submittedIdentifier) ?: return@runWithMinimumResponseTime
            val user = concealOrdinaryFailure { users.findUser(identifier) } ?: return@runWithMinimumResponseTime
            if (concealOrdinaryFailure { throttling.allowPasswordReset(user.email) } != true) {
                return@runWithMinimumResponseTime
            }
            val token = concealOrdinaryFailure { passwordResets.create(user.id) } ?: return@runWithMinimumResponseTime
            try {
                mail.sendPasswordReset(user.email, token, sourceAddress)
            } catch (_: IOException) {
                passwordResets.deleteIfMatches(user.id, token)
            } catch (_: IllegalStateException) {
                passwordResets.deleteIfMatches(user.id, token)
            }
        }
    }

    fun passwordResetUser(
        actor: Actor,
        token: PasswordResetToken,
    ): UserId? {
        actor.requireAnonymous()
        return passwordResets.findUser(token)
    }

    fun resetPassword(
        actor: Actor,
        token: PasswordResetToken,
        password: PlainTextPassword,
        confirmedPassword: String,
    ) {
        actor.requireAnonymous()
        if (password.value != confirmedPassword) throw UserConflict("Password was not confirmed")
        val claim = passwordResets.claim(token) ?: throw UserConflict("Password reset token is invalid or expired")
        val passwordChange = users.preparePasswordChange(claim.userId, password)
        users.persistClaimedPasswordChange(passwordChange, claim)
    }

    /** Blocks the request's virtual thread for 3–4.5 seconds so account existence is not observable. */
    private fun runWithMinimumResponseTime(operation: () -> Unit) {
        val startedAt = System.nanoTime()
        val responseTime = MINIMUM_RESPONSE_TIME_MILLISECONDS + SECURE_RANDOM.nextInt(RESPONSE_TIME_JITTER_MILLISECONDS)
        try {
            operation()
        } finally {
            val elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)
            val remaining = responseTime - elapsed
            if (remaining > 0) Thread.sleep(remaining)
        }
    }

    private fun <T> concealOrdinaryFailure(operation: () -> T): T? =
        try {
            operation()
        } catch (_: Exception) {
            null
        }

    private companion object {
        const val MINIMUM_RESPONSE_TIME_MILLISECONDS = 3_000L
        const val RESPONSE_TIME_JITTER_MILLISECONDS = 1_500
        val SECURE_RANDOM = SecureRandom()
    }
}

private fun passwordResetIdentifier(submittedIdentifier: String): UserIdentifier? {
    val normalized = submittedIdentifier.trim().lowercase()
    return parseUserIdentifier(normalized, ::Cid) ?: parseUserIdentifier(normalized, ::Email)
}

private fun <T : UserIdentifier> parseUserIdentifier(
    value: String,
    parse: (String) -> T,
): T? =
    try {
        parse(value)
    } catch (_: IllegalArgumentException) {
        null
    }

private fun Actor.requireAnonymous() {
    if (this != Actor.Anonymous) throw AccessDenied()
}
