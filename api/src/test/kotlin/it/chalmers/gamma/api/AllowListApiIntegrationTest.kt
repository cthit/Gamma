package it.chalmers.gamma.api

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.ActivationCodes
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import java.util.concurrent.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AllowListApiIntegrationTest {
    @Test
    fun `accepted CIDs persist while an existing user is rejected without insertion`() {
        withDatabase { database ->
            val allowList = AllowListApi(ActivationCodes(database))

            val failures = allowList.allow(listOf("batchfirst", "mscott", "batchlast"))

            assertEquals(listOf("mscott"), failures)
            val allowed = allowList.allowedCids()
            assertTrue("batchfirst" in allowed)
            assertTrue("batchlast" in allowed)
            assertFalse("mscott" in allowed)
        }
    }

    @Test
    fun `a SQL failure leaves earlier and later accepted CIDs committed`() {
        withDatabase { database ->
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_batch_cid() RETURNS trigger LANGUAGE plpgsql AS ${'$'}${'$'}
                BEGIN
                    IF NEW.cid = 'batchbad' THEN
                        RAISE EXCEPTION 'forced allow-list failure';
                    END IF;
                    RETURN NEW;
                END;
                ${'$'}${'$'};
                CREATE TRIGGER reject_batch_cid BEFORE INSERT ON g_allow_list
                FOR EACH ROW EXECUTE FUNCTION reject_batch_cid();
                """.trimIndent(),
            )
            val allowList = AllowListApi(ActivationCodes(database))

            val failures = allowList.allow(listOf("batchfirst", "batchbad", "batchlast"))

            assertEquals(listOf("batchbad"), failures)
            val allowed = allowList.allowedCids()
            assertTrue("batchfirst" in allowed)
            assertTrue("batchlast" in allowed)
            assertFalse("batchbad" in allowed)
        }
    }

    @Test
    fun `invalid and duplicate CIDs are reported without discarding accepted items`() {
        withDatabase { database ->
            val allowList = AllowListApi(ActivationCodes(database))

            val failures = allowList.allow(listOf("batchfirst", "not-a-cid", "batchfirst", "batchlast"))

            assertEquals(listOf("not-a-cid", "batchfirst"), failures)
            val allowed = allowList.allowedCids()
            assertEquals(1, allowed.count { it == "batchfirst" })
            assertTrue("batchlast" in allowed)
        }
    }

    @Test
    fun `cancellation escapes the batch instead of becoming a rejected CID`() {
        val cancellation = CancellationException("stop batch")
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource, listOf(RejectAllowListStatement(cancellation))).use { database ->
                val allowList = AllowListApi(ActivationCodes(database))

                val failure = assertFailsWith<CancellationException> { allowList.allow(listOf("batchfirst")) }

                assertSame(cancellation, failure)
            }
        }
    }

    @Test
    fun `interruption escapes the batch instead of becoming a rejected CID`() {
        val interruption = InterruptedException("stop batch")
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(postgres.dataSource, listOf(RejectAllowListStatement(interruption))).use { database ->
                val allowList = AllowListApi(ActivationCodes(database))

                val failure = assertFailsWith<InterruptedException> { allowList.allow(listOf("batchfirst")) }

                assertSame(interruption, failure)
            }
        }
    }

    private fun withDatabase(operation: (DatabaseFactory) -> Unit) {
        PostgresTestEnvironment().use { postgres ->
            DatabaseFactory(
                DatabaseSettings(postgres.jdbcUrl, postgres.username, postgres.password),
            ).use(operation)
        }
    }
}

private class RejectAllowListStatement(
    private val failure: Exception,
) : StatementInterceptor {
    override fun beforeExecution(
        transaction: Transaction,
        context: StatementContext,
    ): Unit = throw failure
}
