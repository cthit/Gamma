package it.chalmers.gamma.users

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PasswordResetsIntegrationTest {
    @Test
    fun `lookup binds raw and digest tokens to their user and excludes replaced and unknown tokens`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val initial = database.seedPasswordResetForTest(user.id)
            val replacement = database.seedPasswordResetForTest(user.id)
            val other = database.seedPasswordResetForTest(FIXTURE_ADMINISTRATOR_ID)
            assertNull(resets.findUser(initial))
            assertEquals(user.id, resets.findUser(replacement))
            database.executeSqlScript(
                "UPDATE g_password_reset SET token = '${storedToken(replacement.value)}' " +
                    "WHERE user_id = '${user.id.value}'",
            )
            assertEquals(user.id, resets.findUser(replacement))
            assertEquals(FIXTURE_ADMINISTRATOR_ID, resets.findUser(other))
            assertNull(resets.findUser(PasswordResetToken("x".repeat(72))))
        }

    @Test
    fun `lookup rejects expired tokens while retaining current tokens`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val expired = database.seedPasswordResetForTest(user.id)
            val current = database.seedPasswordResetForTest(FIXTURE_ADMINISTRATOR_ID)
            database.executeSqlScript(
                "UPDATE g_password_reset SET created_at = " +
                    "clock_timestamp() AT TIME ZONE 'UTC' - INTERVAL '16 minutes' " +
                    "WHERE user_id = '${user.id.value}'",
            )
            assertNull(resets.findUser(expired))
            assertEquals(FIXTURE_ADMINISTRATOR_ID, resets.findUser(current))
        }

    @Test
    fun `lookup rejects an ambient transaction without changing stored tokens`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val token = database.seedPasswordResetForTest(user.id)
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { resets.findUser(token) }
            }
            assertEquals(user.id, resets.findUser(token))
        }
}
