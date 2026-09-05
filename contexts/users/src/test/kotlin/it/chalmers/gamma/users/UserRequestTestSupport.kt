package it.chalmers.gamma.users

import it.chalmers.gamma.throttling.FixedWindowThrottling
import it.chalmers.gamma.throttling.ThrottleKey
import java.time.Duration

internal class RequestTestThrottle(
    private val outcome: () -> Boolean = { true },
) : FixedWindowThrottling {
    val charges = mutableListOf<RequestTestCharge>()

    override fun charge(
        key: ThrottleKey,
        maximumAttempts: Int,
        window: Duration,
    ): Boolean {
        charges += RequestTestCharge(key, maximumAttempts, window)
        return outcome()
    }
}

internal data class RequestTestCharge(
    val key: ThrottleKey,
    val maximumAttempts: Int,
    val window: Duration,
)

internal class RequestTestMail(
    private val activation: (
        Cid,
        RegistrationToken,
        String?,
    ) -> Unit = { _, _, _ -> throw AssertionError("Unexpected activation mail") },
    private val reset: (
        Email,
        PasswordResetToken,
        String?,
    ) -> Unit = { _, _, _ -> throw AssertionError("Unexpected reset mail") },
) : UserMail {
    override fun sendActivation(
        cid: Cid,
        token: RegistrationToken,
        sourceAddress: String?,
    ) = activation(cid, token, sourceAddress)

    override fun sendPasswordReset(
        email: Email,
        token: PasswordResetToken,
        sourceAddress: String?,
    ) = reset(email, token, sourceAddress)
}
