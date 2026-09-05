package it.chalmers.gamma.users

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserBootstrapIntegrationTest {
    @Test
    fun `bootstrap hashes without a connection and commits one user with both flags`() =
        withUserDatabase(maximumPoolSize = 1, loadRegressionFixture = false) { database ->
            val activations = ActivationCodes(database)
            activations.allow(Cid("admin"))
            val token = database.seedActivationForTest(Cid("admin"))
            var hashes = 0
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashes++
                        assertTrue(database.ping())
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            val operation = UserBootstrap(database, hasher)
            assertEquals(AdministratorBootstrapResult.CREATED, operation.ensureAdministrator(password))
            val queries = UserQueries(database)
            val user = assertNotNull(queries.findUser(Cid("admin")))
            assertEquals(Email("admin@chalmers.it"), user.email)
            assertEquals(2018, user.acceptanceYear.value)
            assertEquals(Language.EN, user.language)
            assertEquals(0, user.version)
            assertEquals(false, user.locked)
            database.commitTransaction(readOnly = true) {
                assertTrue(AdminUsersTable.selectAll().where { AdminUsersTable.userId eq user.id.value }.any())
                assertTrue(
                    GdprTrainedUsersTable.selectAll().where { GdprTrainedUsersTable.userId eq user.id.value }.any(),
                )
            }
            assertNull(activations.findCid(token))
            assertEquals(false, activations.allowedCids().contains(Cid("admin")))
            assertEquals(AdministratorBootstrapResult.ALREADY_CONFIGURED, operation.ensureAdministrator(null))
            assertEquals(1, hashes)
            assertEquals(1, database.tableRowCount("g_user"))
        }

    @Test
    fun `empty bootstrap requires a password before creating any state`() =
        withUserDatabase(loadRegressionFixture = false) { database ->
            assertEquals(
                AdministratorBootstrapResult.PASSWORD_REQUIRED,
                UserBootstrap(database, UnexpectedBootstrapHasher).ensureAdministrator(null),
            )
            assertEquals(0, database.tableRowCount("g_user"))
            assertEquals(0, database.tableRowCount("g_admin_user"))
        }

    @Test
    fun `existing configuration and reserved CID take precedence over a missing password`() =
        withUserDatabase { database ->
            val operation = UserBootstrap(database, UnexpectedBootstrapHasher)
            assertEquals(AdministratorBootstrapResult.ALREADY_CONFIGURED, operation.ensureAdministrator(null))
            database.executeSqlScript("DELETE FROM g_admin_user; UPDATE g_user SET cid = 'admin' WHERE cid = 'mscott'")
            assertEquals(AdministratorBootstrapResult.ADMIN_CID_IN_USE, operation.ensureAdministrator(null))
            assertEquals(0, database.tableRowCount("g_admin_user"))
        }

    @Test
    fun `failed GDPR assignment rolls back user administrator and consumed reservations`() =
        withUserDatabase(loadRegressionFixture = false) { database ->
            val activations = ActivationCodes(database)
            activations.allow(Cid("admin"))
            val token = database.seedActivationForTest(Cid("admin"))
            database.executeSqlScript(
                """
                CREATE FUNCTION reject_bootstrap_gdpr() RETURNS trigger AS ${'$'}${'$'}
                BEGIN RAISE EXCEPTION 'injected GDPR failure'; END;
                ${'$'}${'$'} LANGUAGE plpgsql;
                CREATE TRIGGER reject_bootstrap_gdpr BEFORE INSERT ON g_gdpr_trained
                FOR EACH ROW EXECUTE FUNCTION reject_bootstrap_gdpr();
                """.trimIndent(),
            )
            var hashes = 0
            val hasher =
                object : PasswordHasher by AlwaysMatchingPasswordHasher {
                    override fun hash(password: PlainTextPassword): PasswordHash {
                        hashes++
                        return AlwaysMatchingPasswordHasher.hash(password)
                    }
                }
            assertFailsWith<SQLException> { UserBootstrap(database, hasher).ensureAdministrator(password) }
            assertEquals(1, hashes)
            assertEquals(0, database.tableRowCount("g_user"))
            assertEquals(0, database.tableRowCount("g_admin_user"))
            assertEquals(0, database.tableRowCount("g_gdpr_trained"))
            assertEquals(Cid("admin"), activations.findCid(token))
            assertTrue(activations.allowedCids().contains(Cid("admin")))
        }

    @Test
    fun `bootstrap email conflict leaves the existing identity unchanged`() =
        withUserDatabase { database ->
            database.executeSqlScript(
                "DELETE FROM g_admin_user; UPDATE g_user SET email = 'admin@chalmers.it' WHERE cid = 'mscott'",
            )
            val queries = UserQueries(database)
            val existing = assertNotNull(queries.findUser(Cid("mscott")))
            val failure =
                assertFailsWith<UserConflict> {
                    UserBootstrap(database, AlwaysMatchingPasswordHasher).ensureAdministrator(password)
                }
            assertEquals("Email is already in use", failure.message)
            assertEquals(existing, queries.findUser(existing.id))
            assertNull(queries.findUser(Cid("admin")))
            assertEquals(0, database.tableRowCount("g_admin_user"))
        }

    private val password = PlainTextPassword("bootstrap password")
}

private object UnexpectedBootstrapHasher : PasswordHasher by AlwaysMatchingPasswordHasher {
    override fun hash(password: PlainTextPassword): PasswordHash = error("Unexpected hashing")
}
