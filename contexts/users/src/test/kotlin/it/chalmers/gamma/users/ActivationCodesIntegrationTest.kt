package it.chalmers.gamma.users

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ActivationCodesIntegrationTest {
    @Test
    fun `lookup accepts presented and digest tokens but rejects replaced expired and unknown tokens`() =
        withUserDatabase { database ->
            val tokens = ActivationCodes(database)
            val cid = Cid("student")
            val otherCid = Cid("another")
            tokens.allow(cid)
            tokens.allow(otherCid)
            val old = database.seedActivationForTest(cid)
            val current = database.seedActivationForTest(cid)
            val expired = database.seedActivationForTest(otherCid)
            assertNull(tokens.findCid(old))
            assertEquals(cid, tokens.findCid(current))
            database.executeSqlScript(
                "UPDATE g_user_activation SET token = '${storedToken(current.value)}' WHERE cid = 'student'",
            )
            assertEquals(cid, tokens.findCid(current))
            database.executeSqlScript(
                "UPDATE g_user_activation SET created_at = " +
                    "clock_timestamp() AT TIME ZONE 'UTC' - INTERVAL '16 minutes' WHERE cid = 'another'",
            )
            assertNull(tokens.findCid(expired))
            assertNull(tokens.findCid(RegistrationToken("x".repeat(72))))
            assertEquals(cid, tokens.findCid(current))
            assertTrue(otherCid in tokens.allowedCids())
        }

    @Test
    fun `complete activation reads and allow insertion reject ambient transactions`() =
        withUserDatabase { database ->
            val cid = Cid("student")
            val tokens = ActivationCodes(database)
            tokens.allow(cid)
            val token = database.seedActivationForTest(cid)
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { tokens.allow(Cid("another")) }
                assertFailsWith<IllegalStateException> { tokens.allowedCids() }
                assertFailsWith<IllegalStateException> { tokens.findCid(token) }
            }
            assertFalse(Cid("another") in tokens.allowedCids())
            assertEquals(cid, tokens.findCid(token))
        }
}
