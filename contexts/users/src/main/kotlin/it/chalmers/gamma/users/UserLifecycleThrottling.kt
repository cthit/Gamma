package it.chalmers.gamma.users

import it.chalmers.gamma.throttling.FixedWindowThrottling
import it.chalmers.gamma.throttling.ThrottleKey
import java.time.Duration

class UserLifecycleThrottling(
    private val throttling: FixedWindowThrottling,
) {
    fun allowActivation(cid: Cid): Boolean =
        throttling.charge(
            ThrottleKey.digest("activation", cid.value),
            3,
            Duration.ofHours(24),
        )

    fun allowPasswordReset(email: Email): Boolean =
        throttling.charge(
            ThrottleKey.digest("password-reset", email.value),
            3,
            Duration.ofHours(24),
        )
}
