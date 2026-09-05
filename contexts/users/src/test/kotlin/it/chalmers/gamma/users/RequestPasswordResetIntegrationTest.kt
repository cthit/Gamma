package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.throttling.ThrottleKey
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RequestPasswordResetIntegrationTest {
    @Test
    fun `CID and email requests use the recipient throttle and commit before sending with no connection held`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val throttle =
                RequestTestThrottle {
                    assertTrue(database.ping())
                    true
                }
            var deliveries = 0
            val mail =
                RequestTestMail(reset = { recipient, token, source ->
                    deliveries++
                    assertEquals(user.email, recipient)
                    assertEquals("127.0.0.9", source)
                    assertTrue(database.ping())
                    assertEquals(user.id, PasswordResets(database).findUser(token))
                })
            val operation = RequestPasswordReset(database, throttle, mail)
            for (identifier in listOf(" JHALPERT ", " ${user.email.value.uppercase()} ")) {
                val startedAt = System.nanoTime()
                operation.request(Actor.Anonymous, identifier, "127.0.0.9")
                assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt) >= 3_000)
            }
            assertEquals(2, deliveries)
            val expected =
                RequestTestCharge(ThrottleKey.digest("password-reset", user.email.value), 3, Duration.ofHours(24))
            assertEquals(listOf(expected, expected), throttle.charges)
        }

    @Test
    fun `invalid unknown and throttled recovery requests keep the private response without issuing tokens`() =
        withUserDatabase { database ->
            var allocations = 0
            val throttle = RequestTestThrottle { false }
            val operation =
                RequestPasswordReset(database, throttle, RequestTestMail()) {
                    allocations++
                    "r".repeat(72)
                }
            for (identifier in listOf("invalid identifier", "missinguser", "jhalpert")) {
                val startedAt = System.nanoTime()
                operation.request(Actor.Anonymous, identifier)
                assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt) >= 3_000)
            }
            assertEquals(1, throttle.charges.size)
            assertEquals(0, allocations)
            assertEquals(0L, database.tableRowCount("g_password_reset"))
        }

    @Test
    fun `ordinary mail failure withdraws only this requests token and preserves a replacement`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            for (replace in listOf(false, true)) {
                var issued: PasswordResetToken? = null
                var replacement: PasswordResetToken? = null
                val mail =
                    RequestTestMail(reset = { _, token, _ ->
                        issued = token
                        if (replace) replacement = database.seedPasswordResetForTest(user.id)
                        throw IOException("delivery failed")
                    })
                RequestPasswordReset(database, RequestTestThrottle(), mail).request(Actor.Anonymous, "jhalpert")
                assertNull(resets.findUser(assertNotNull(issued)))
                replacement?.let { assertEquals(user.id, resets.findUser(it)) }
            }
        }

    @Test
    fun `failed issuance restores the previous reset token without repeated generation or mail`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val previous = database.seedPasswordResetForTest(user.id)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_recovery_request() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected recovery failure' USING ERRCODE = '40001'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_recovery_request AFTER INSERT OR UPDATE ON g_password_reset
                FOR EACH ROW EXECUTE FUNCTION reject_recovery_request();
                """.trimIndent(),
            )
            var allocations = 0
            val operation =
                RequestPasswordReset(database, RequestTestThrottle(), RequestTestMail()) {
                    allocations++
                    "r".repeat(72)
                }
            operation.request(Actor.Anonymous, "jhalpert")
            assertEquals(1, allocations)
            assertEquals(user.id, resets.findUser(previous))
        }

    @Test
    fun `cleanup failure retains the token and does not expose a different ordinary response`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_recovery_cleanup() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected recovery cleanup failure'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_recovery_cleanup BEFORE DELETE ON g_password_reset
                FOR EACH ROW EXECUTE FUNCTION reject_recovery_cleanup();
                """.trimIndent(),
            )
            var issued: PasswordResetToken? = null
            val mail =
                RequestTestMail(reset = { _, token, _ ->
                    issued = token
                    throw IOException("delivery failed")
                })
            RequestPasswordReset(database, RequestTestThrottle(), mail).request(Actor.Anonymous, "jhalpert")
            assertEquals(user.id, PasswordResets(database).findUser(assertNotNull(issued)))
        }

    @Test
    fun `authenticated and ambient recovery requests fail before throttling or token allocation`() =
        withUserDatabase { database ->
            val throttle = RequestTestThrottle { throw AssertionError("Unexpected throttle") }
            val operation =
                RequestPasswordReset(database, throttle, RequestTestMail()) {
                    throw AssertionError("Unexpected token allocation")
                }
            assertFailsWith<AccessDenied> {
                operation.request(Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value)), "jhalpert")
            }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { operation.request(Actor.Anonymous, "jhalpert") }
            }
        }

    @Test
    fun `throttle cancellation and interruption escape recovery requests`() =
        withUserDatabase { database ->
            for (failure in listOf(CancellationException("cancelled"), InterruptedException("interrupted"))) {
                val throttle = RequestTestThrottle { throw failure }
                val operation = RequestPasswordReset(database, throttle, RequestTestMail())
                val startedAt = System.nanoTime()
                val thrown = assertFailsWith<Exception> { operation.request(Actor.Anonymous, "jhalpert") }
                assertSame(failure, thrown)
                assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt) < 3_000)
            }
        }

    @Test
    fun `mail cancellation escapes recovery requests`() =
        withUserDatabase { database ->
            val failure = CancellationException("cancelled")
            var issued: PasswordResetToken? = null
            val mail =
                RequestTestMail(reset = { _, token, _ ->
                    issued = token
                    throw failure
                })
            val operation = RequestPasswordReset(database, RequestTestThrottle(), mail)
            val thrown =
                assertFailsWith<CancellationException> {
                    operation.request(Actor.Anonymous, "jhalpert")
                }
            assertSame(failure, thrown)
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            assertEquals(user.id, PasswordResets(database).findUser(assertNotNull(issued)))
        }

    @Test
    fun `email changes during throttling prevent issuing recovery to an outdated recipient`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val throttle =
                RequestTestThrottle {
                    UpdateMyEmail(database).update(user.profileActor(), Email("new.recipient@example.org"))
                    true
                }
            var deliveries = 0
            val operation =
                RequestPasswordReset(
                    database,
                    throttle,
                    RequestTestMail(reset = {
                        _,
                        _,
                        _,
                        ->
                        deliveries++
                    }),
                )
            operation.request(Actor.Anonymous, "jhalpert")
            assertEquals(0, deliveries)
            assertEquals(0L, database.tableRowCount("g_password_reset"))
        }
}
