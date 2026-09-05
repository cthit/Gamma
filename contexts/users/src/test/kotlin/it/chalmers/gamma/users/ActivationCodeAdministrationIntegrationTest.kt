package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import java.sql.SQLException
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ActivationCodeAdministrationIntegrationTest {
    @Test
    fun `allow rejects duplicate and existing-user CIDs without retaining rejected reservations`() =
        withUserDatabase { database ->
            val operation = ActivationCodeAdministration(database)
            val tokens = ActivationCodes(database)
            val cid = Cid("student")
            operation.allowCid(administrator, cid)
            assertTrue(tokens.allowedCids().contains(cid))
            assertFailsWith<UserConflict> { operation.allowCid(administrator, cid) }
            assertFailsWith<UserConflict> { operation.allowCid(administrator, Cid("mscott")) }
            assertFalse(tokens.allowedCids().contains(Cid("mscott")))
        }

    @Test
    fun `administrative lists are ordered and pending activations retain creation times`() =
        withUserDatabase { database ->
            val operation = ActivationCodeAdministration(database)
            val cids = listOf(Cid("zebra"), Cid("alice"))
            for (cid in cids) {
                operation.allowCid(administrator, cid)
                database.seedActivationForTest(cid)
            }
            database.executeSqlScript(
                "UPDATE g_user_activation SET created_at = '2025-01-02 03:04:05' WHERE cid = 'alice'",
            )
            val allowed = operation.allowedCids(administrator).filter { it in cids }
            val pending = operation.pendingActivations(administrator).filter { it.cid in cids }
            assertEquals(listOf(Cid("alice"), Cid("zebra")), allowed)
            assertEquals(allowed, pending.map { it.cid })
            assertEquals(Instant.parse("2025-01-02T03:04:05Z"), pending.first().createdAt)
        }

    @Test
    fun `deleting an activation preserves eligibility and permits reissuance`() =
        withUserDatabase { database ->
            val operation = ActivationCodeAdministration(database)
            val tokens = ActivationCodes(database)
            val cid = Cid("student")
            operation.allowCid(administrator, cid)
            val token = database.seedActivationForTest(cid)
            operation.deleteActivation(administrator, cid)
            assertNull(tokens.findCid(token))
            assertTrue(tokens.allowedCids().contains(cid))
            assertFailsWith<UserNotFound> { operation.deleteActivation(administrator, cid) }
            assertEquals(cid, tokens.findCid(database.seedActivationForTest(cid)))
        }

    @Test
    fun `retraction removes eligibility and activation together and rejects an unknown CID`() =
        withUserDatabase { database ->
            val operation = ActivationCodeAdministration(database)
            val tokens = ActivationCodes(database)
            val cid = Cid("student")
            operation.allowCid(administrator, cid)
            val token = database.seedActivationForTest(cid)
            operation.retractCid(administrator, cid)
            assertFalse(tokens.allowedCids().contains(cid))
            assertNull(tokens.findCid(token))
            assertFailsWith<UserNotFound> { operation.retractCid(administrator, cid) }
        }

    @Test
    fun `failed retraction restores the activation already deleted in its transaction`() =
        withUserDatabase { database ->
            val operation = ActivationCodeAdministration(database)
            val tokens = ActivationCodes(database)
            val cid = Cid("student")
            operation.allowCid(administrator, cid)
            val token = database.seedActivationForTest(cid)
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_retraction() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected retraction failure'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_retraction BEFORE DELETE ON g_allow_list
                FOR EACH ROW EXECUTE FUNCTION reject_retraction();
                """.trimIndent(),
            )
            assertFailsWith<SQLException> { operation.retractCid(administrator, cid) }
            assertEquals(cid, tokens.findCid(token))
            assertTrue(tokens.allowedCids().contains(cid))
        }

    @Test
    fun `all administrative operations require current authority before exposing target state`() =
        withUserDatabase { database ->
            val operation = ActivationCodeAdministration(database)
            val tokens = ActivationCodes(database)
            val cid = Cid("student")
            operation.allowCid(administrator, cid)
            val token = database.seedActivationForTest(cid)
            val member = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            UserAccessFlags(database).replace(administrator, UserAccessFlagKind.ADMINISTRATOR, setOf(member.id))
            for (actor in listOf(Actor.Anonymous, administrator)) {
                assertFailsWith<AccessDenied> { operation.allowCid(actor, cid) }
                assertFailsWith<AccessDenied> { operation.retractCid(actor, Cid("missing")) }
                assertFailsWith<AccessDenied> { operation.deleteActivation(actor, Cid("missing")) }
                assertFailsWith<AccessDenied> { operation.allowedCids(actor) }
                assertFailsWith<AccessDenied> { operation.pendingActivations(actor) }
            }
            assertEquals(cid, tokens.findCid(token))
            assertTrue(tokens.allowedCids().contains(cid))
        }

    @Test
    fun `administrative commands and queries reject an ambient transaction`() =
        withUserDatabase { database ->
            val operation = ActivationCodeAdministration(database)
            val tokens = ActivationCodes(database)
            val cid = Cid("student")
            operation.allowCid(administrator, cid)
            val token = database.seedActivationForTest(cid)
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { operation.allowCid(administrator, Cid("another")) }
                assertFailsWith<IllegalStateException> { operation.retractCid(administrator, cid) }
                assertFailsWith<IllegalStateException> { operation.deleteActivation(administrator, cid) }
                assertFailsWith<IllegalStateException> { operation.allowedCids(administrator) }
                assertFailsWith<IllegalStateException> { operation.pendingActivations(administrator) }
            }
            assertFalse(tokens.allowedCids().contains(Cid("another")))
            assertEquals(cid, tokens.findCid(token))
        }

    private val administrator = Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value), true)
}
