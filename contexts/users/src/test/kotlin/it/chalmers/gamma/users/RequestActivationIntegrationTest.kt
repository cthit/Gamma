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

class RequestActivationIntegrationTest {
    @Test
    fun `activation checks its throttle commits before mail and preserves the minimum response delay`() =
        withUserDatabase(maximumPoolSize = 1) { database ->
            val cid = Cid("student")
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val throttle =
                RequestTestThrottle {
                    assertTrue(database.ping())
                    true
                }
            var deliveries = 0
            val mail =
                RequestTestMail(activation = { recipient, token, source ->
                    deliveries++
                    assertEquals(cid, recipient)
                    assertEquals("127.0.0.9", source)
                    assertTrue(database.ping())
                    assertEquals(cid, activations.findCid(token))
                })
            val startedAt = System.nanoTime()
            RequestActivation(database, throttle, mail).request(Actor.Anonymous, cid, "127.0.0.9")
            assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt) >= 3_000)
            assertEquals(1, deliveries)
            assertEquals(
                listOf(RequestTestCharge(ThrottleKey.digest("activation", cid.value), 3, Duration.ofHours(24))),
                throttle.charges,
            )
        }

    @Test
    fun `unknown disallowed and throttled activation requests do not issue or deliver a token`() =
        withUserDatabase { database ->
            val cid = Cid("student")
            var allocations = 0
            val throttle = RequestTestThrottle { false }
            val operation =
                RequestActivation(database, throttle, RequestTestMail()) {
                    allocations++
                    "a".repeat(72)
                }
            val startedAt = System.nanoTime()
            operation.request(Actor.Anonymous, cid)
            assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt) >= 3_000)
            assertEquals(0, throttle.charges.size)
            ActivationCodes(database).allow(cid)
            operation.request(Actor.Anonymous, cid)
            assertEquals(1, throttle.charges.size)
            assertEquals(0, allocations)
            assertEquals(0L, database.tableRowCount("g_user_activation"))
        }

    @Test
    fun `retraction during throttling prevents a later activation issuance`() =
        withUserDatabase { database ->
            val cid = Cid("student")
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val throttle =
                RequestTestThrottle {
                    ActivationCodeAdministration(database).retractCid(
                        Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value)),
                        cid,
                    )
                    true
                }
            RequestActivation(database, throttle, RequestTestMail()).request(Actor.Anonymous, cid)
            assertEquals(0L, database.tableRowCount("g_user_activation"))
        }

    @Test
    fun `ordinary mail failure withdraws only the issued activation and preserves replacements`() =
        withUserDatabase { database ->
            val cid = Cid("student")
            val activations = ActivationCodes(database)
            activations.allow(cid)
            for (replace in listOf(false, true)) {
                var issued: RegistrationToken? = null
                var replacement: RegistrationToken? = null
                val mail =
                    RequestTestMail(activation = { _, token, _ ->
                        issued = token
                        if (replace) replacement = database.seedActivationForTest(cid)
                        throw IOException("delivery failed")
                    })
                RequestActivation(database, RequestTestThrottle(), mail).request(Actor.Anonymous, cid)
                assertNull(activations.findCid(assertNotNull(issued)))
                replacement?.let { assertEquals(cid, activations.findCid(it)) }
                assertTrue(activations.allowedCids().contains(cid))
            }
        }

    @Test
    fun `issuance failure rolls back replacement without repeating token generation or sending mail`() =
        withUserDatabase { database ->
            val cid = Cid("student")
            val activations = ActivationCodes(database)
            activations.allow(cid)
            val previous = database.seedActivationForTest(cid)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_activation_request() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected activation failure' USING ERRCODE = '40001'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_activation_request AFTER INSERT OR UPDATE ON g_user_activation
                FOR EACH ROW EXECUTE FUNCTION reject_activation_request();
                """.trimIndent(),
            )
            var allocations = 0
            val operation =
                RequestActivation(database, RequestTestThrottle(), RequestTestMail()) {
                    allocations++
                    "a".repeat(72)
                }
            operation.request(Actor.Anonymous, cid)
            assertEquals(1, allocations)
            assertEquals(cid, activations.findCid(previous))
        }

    @Test
    fun `cleanup failures retain the token and keep the ordinary private response`() =
        withUserDatabase { database ->
            val cid = Cid("student")
            val activations = ActivationCodes(database)
            activations.allow(cid)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_activation_cleanup() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected cleanup failure'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_activation_cleanup BEFORE DELETE ON g_user_activation
                FOR EACH ROW EXECUTE FUNCTION reject_activation_cleanup();
                """.trimIndent(),
            )
            var issued: RegistrationToken? = null
            val mail =
                RequestTestMail(activation = { _, token, _ ->
                    issued = token
                    throw IOException("mail failed")
                })
            RequestActivation(database, RequestTestThrottle(), mail).request(Actor.Anonymous, cid)
            assertEquals(cid, activations.findCid(assertNotNull(issued)))
        }

    @Test
    fun `authenticated and ambient activation requests fail before throttling or token allocation`() =
        withUserDatabase { database ->
            val throttle = RequestTestThrottle { throw AssertionError("Unexpected throttle") }
            val operation =
                RequestActivation(database, throttle, RequestTestMail()) {
                    throw AssertionError("Unexpected token allocation")
                }
            assertFailsWith<AccessDenied> {
                operation.request(Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value)), Cid("student"))
            }
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { operation.request(Actor.Anonymous, Cid("student")) }
            }
        }

    @Test
    fun `throttle cancellation and interruption escape activation requests`() =
        withUserDatabase { database ->
            val cid = Cid("student")
            ActivationCodes(database).allow(cid)
            for (failure in listOf(CancellationException("cancelled"), InterruptedException("interrupted"))) {
                val throttle = RequestTestThrottle { throw failure }
                val operation = RequestActivation(database, throttle, RequestTestMail())
                val startedAt = System.nanoTime()
                val thrown = assertFailsWith<Exception> { operation.request(Actor.Anonymous, cid) }
                assertSame(failure, thrown)
                assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt) < 3_000)
            }
        }

    @Test
    fun `mail cancellation escapes activation requests`() =
        withUserDatabase { database ->
            val cid = Cid("student")
            ActivationCodes(database).allow(cid)
            val failure = CancellationException("cancelled")
            var issued: RegistrationToken? = null
            val mail =
                RequestTestMail(activation = { _, token, _ ->
                    issued = token
                    throw failure
                })
            val operation = RequestActivation(database, RequestTestThrottle(), mail)
            val thrown = assertFailsWith<CancellationException> { operation.request(Actor.Anonymous, cid) }
            assertSame(failure, thrown)
            assertEquals(cid, ActivationCodes(database).findCid(assertNotNull(issued)))
        }
}
