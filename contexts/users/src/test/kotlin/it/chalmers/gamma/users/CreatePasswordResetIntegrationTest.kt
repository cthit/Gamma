package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CreatePasswordResetIntegrationTest {
    @Test
    fun `issuance returns the locked user details and replaces only that users token`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val previous = database.seedPasswordResetForTest(user.id)
            val other = database.seedPasswordResetForTest(FIXTURE_ADMINISTRATOR_ID)
            val issued = CreatePasswordReset(database).create(administrator, user.id)
            assertEquals(user.id, issued.user.id)
            assertEquals(user.cid, issued.user.cid)
            assertEquals(user.nick, issued.user.nick)
            assertEquals(user.firstName, issued.user.firstName)
            assertEquals(user.lastName, issued.user.lastName)
            assertEquals(user.acceptanceYear, issued.user.acceptanceYear)
            assertEquals(user.version, issued.user.version)
            assertEquals(user, UserQueries(database).findUser(user.id))
            assertNull(resets.findUser(previous))
            assertEquals(user.id, resets.findUser(issued.token))
            assertEquals(FIXTURE_ADMINISTRATOR_ID, resets.findUser(other))
            assertEquals("IssuedPasswordReset(<redacted>)", issued.toString())
        }

    @Test
    fun `authority is checked before revealing whether the target exists`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val token = database.seedPasswordResetForTest(user.id)
            val operation = CreatePasswordReset(database)
            val missing = UserId.generate()
            assertFailsWith<AccessDenied> { operation.create(Actor.Anonymous, user.id) }
            assertFailsWith<AccessDenied> { operation.create(user.profileActor(isAdministrator = true), user.id) }
            assertFailsWith<AccessDenied> { operation.create(user.profileActor(isAdministrator = true), missing) }
            assertFailsWith<UserNotFound> { operation.create(administrator, missing) }
            assertEquals(user.id, resets.findUser(token))
        }

    @Test
    fun `demotion before persistence rejects issuance and preserves the existing token`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val token = database.seedPasswordResetForTest(user.id)
            val operation =
                CreatePasswordReset(database) {
                    UserAccessFlags(database).replace(administrator, UserAccessFlagKind.ADMINISTRATOR, setOf(user.id))
                    "r".repeat(72)
                }
            assertFailsWith<AccessDenied> { operation.create(administrator, user.id) }
            assertEquals(user.id, resets.findUser(token))
        }

    @Test
    fun `failed replacement rolls back and allocates one token across transaction retries`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val token = database.seedPasswordResetForTest(user.id)
            database.executeSqlScript(
                """
                CREATE SEQUENCE reset_attempts;
                CREATE FUNCTION reject_reset_issuance() RETURNS trigger AS ${'$'}${'$'}
                BEGIN
                    PERFORM nextval('reset_attempts');
                    RAISE EXCEPTION 'injected reset issuance failure' USING ERRCODE = '40001';
                END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_reset_issuance AFTER INSERT OR UPDATE ON g_password_reset
                FOR EACH ROW EXECUTE FUNCTION reject_reset_issuance();
                """.trimIndent(),
            )
            var allocations = 0
            val operation =
                CreatePasswordReset(database) {
                    allocations++
                    "r".repeat(72)
                }
            assertFailsWith<SQLException> { operation.create(administrator, user.id) }
            assertEquals(1, allocations)
            assertEquals(user.id, resets.findUser(token))
            assertNull(resets.findUser(PasswordResetToken("r".repeat(72))))
            val attempts =
                database.commitTransaction(readOnly = true) {
                    exec("SELECT last_value FROM reset_attempts") { rows ->
                        rows.next()
                        rows.getLong(1)
                    }
                }
            assertEquals(3L, attempts)
            assertEquals(user, UserQueries(database).findUser(user.id))
        }

    @Test
    fun `an ambient transaction cannot hide an uncommitted reset link`() =
        withUserDatabase { database ->
            val user = assertNotNull(UserQueries(database).findUser(Cid("jhalpert")))
            val resets = PasswordResets(database)
            val token = database.seedPasswordResetForTest(user.id)
            database.commitTransaction {
                assertFailsWith<IllegalStateException> { CreatePasswordReset(database).create(administrator, user.id) }
            }
            assertEquals(user.id, resets.findUser(token))
        }

    private val administrator = Actor.User(ActorUserId(FIXTURE_ADMINISTRATOR_ID.value))
}
