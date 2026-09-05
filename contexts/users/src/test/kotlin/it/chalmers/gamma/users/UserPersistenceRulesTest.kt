package it.chalmers.gamma.users

import org.postgresql.util.PSQLException
import org.postgresql.util.ServerErrorMessage
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class UserPersistenceRulesTest {
    @Test
    fun `structured cid constraint survives an intermediate exception`() {
        val conflict =
            assertFailsWith<UserConflict> {
                run {
                    translateUserUniqueConflict<Unit> {
                        throw IllegalStateException("wrapped", uniqueViolation(CID_CONSTRAINT, "lokaliserat fel"))
                    }
                }
            }

        assertEquals("CID is already in use", conflict.message)
    }

    @Test
    fun `structured email constraint survives the jdbc next-exception chain`() {
        val outerFailure = SQLException("lokaliserat fel", UNIQUE_VIOLATION_STATE)
        outerFailure.setNextException(uniqueViolation(EMAIL_CONSTRAINT, "annat meddelande"))

        val conflict =
            assertFailsWith<UserConflict> {
                run { translateUserUniqueConflict<Unit> { throw outerFailure } }
            }

        assertEquals("Email is already in use", conflict.message)
    }

    @Test
    fun `legacy message matching remains a compatibility fallback`() {
        val conflict =
            assertFailsWith<UserConflict> {
                run {
                    translateUserUniqueConflict<Unit> {
                        throw SQLException("duplicate key violates $CID_CONSTRAINT", UNIQUE_VIOLATION_STATE)
                    }
                }
            }

        assertEquals("CID is already in use", conflict.message)
    }

    @Test
    fun `unrelated cyclic sql failures pass through unchanged`() {
        val unrelatedFailure =
            object : SQLException("lokaliserat fel", UNIQUE_VIOLATION_STATE) {
                override fun getNextException(): SQLException = this
            }

        val thrown =
            assertFailsWith<SQLException> {
                run { translateUserUniqueConflict<Unit> { throw unrelatedFailure } }
            }

        assertSame(unrelatedFailure, thrown)
    }

    @Test
    fun `application failures pass through unchanged`() {
        val failure = IllegalStateException("failed")

        val thrown =
            assertFailsWith<IllegalStateException> {
                run { translateUserUniqueConflict<Unit> { throw failure } }
            }

        assertSame(failure, thrown)
    }
}

private fun uniqueViolation(
    constraint: String,
    message: String,
): PSQLException =
    PSQLException(
        ServerErrorMessage("SERROR\u0000C$UNIQUE_VIOLATION_STATE\u0000M$message\u0000n$constraint\u0000\u0000"),
    )

private const val UNIQUE_VIOLATION_STATE = "23505"
private const val CID_CONSTRAINT = "g_user_cid_key"
private const val EMAIL_CONSTRAINT = "g_user_email_key"
