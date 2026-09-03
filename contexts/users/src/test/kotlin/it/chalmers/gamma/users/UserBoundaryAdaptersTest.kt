package it.chalmers.gamma.users

import it.chalmers.gamma.throttling.FixedWindowThrottling
import it.chalmers.gamma.throttling.ThrottleKey
import it.chalmers.gamma.users.Cid
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserBoundaryAdaptersTest {
    @Test
    fun `lifecycle throttling maps activation and recovery policies to their boundary keys`() =
        run {
            val fixedWindow = RecordingFixedWindowThrottling(results = listOf(true, false))
            val rateLimit = UserLifecycleThrottling(fixedWindow)
            val cid = Cid("newstudent")
            val email = Email("student@example.org")

            assertTrue(rateLimit.allowActivation(cid))
            assertFalse(rateLimit.allowPasswordReset(email))

            assertEquals(
                RecordedCharge(
                    key = ThrottleKey.digest("activation", cid.value),
                    maximumAttempts = 3,
                    window = Duration.ofHours(24),
                ),
                fixedWindow.charges[0],
            )
            assertEquals(
                RecordedCharge(
                    key = ThrottleKey.digest("password-reset", email.value),
                    maximumAttempts = 3,
                    window = Duration.ofHours(24),
                ),
                fixedWindow.charges[1],
            )
        }
}

private data class RecordedCharge(
    val key: ThrottleKey,
    val maximumAttempts: Int,
    val window: Duration,
)

private class RecordingFixedWindowThrottling(
    results: List<Boolean>,
) : FixedWindowThrottling {
    private val results = ArrayDeque(results)
    val charges = mutableListOf<RecordedCharge>()

    override fun charge(
        key: ThrottleKey,
        maximumAttempts: Int,
        window: Duration,
    ): Boolean {
        charges += RecordedCharge(key, maximumAttempts, window)
        return results.removeFirst()
    }
}
