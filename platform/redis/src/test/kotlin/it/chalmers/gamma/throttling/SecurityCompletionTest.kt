package it.chalmers.gamma.throttling

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class SecurityCompletionTest {
    @Test
    fun `ordinary completion failure propagates while caller remains active`() {
        val failure = CompletionFailure()

        val observed =
            assertFailsWith<CompletionFailure> {
                run { completeSecurityOperation { throw failure } }
            }

        assertSame(failure, observed)
    }

    private class CompletionFailure : RuntimeException()
}
