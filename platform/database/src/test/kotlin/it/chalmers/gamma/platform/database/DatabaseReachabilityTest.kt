package it.chalmers.gamma.platform.database

import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DatabaseReachabilityTest {
    @Test
    fun `reports successful and unavailable database connections`() {
        assertTrue(databaseIsReachable { true })
        assertFalse(databaseIsReachable { throw SQLException("database unavailable") })
    }

    @Test
    fun `preserves unexpected failures`() {
        val runtimeFailure = IllegalStateException("unexpected readiness failure")
        val fatalFailure = AssertionError("fatal readiness failure")

        assertSame(runtimeFailure, capturedFailure { databaseIsReachable { throw runtimeFailure } })
        assertSame(fatalFailure, capturedFailure { databaseIsReachable { throw fatalFailure } })
    }

    private fun capturedFailure(operation: () -> Unit): Throwable =
        try {
            operation()
            error("Expected the operation to fail")
        } catch (failure: Throwable) {
            failure
        }
}
