package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.apiaccess.CreateApiKey
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.users.AdministratorBootstrapResult
import it.chalmers.gamma.users.BcryptPasswordHasher
import it.chalmers.gamma.users.PasswordHash
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.UserBootstrap
import org.springframework.boot.DefaultApplicationArguments
import tools.jackson.databind.ObjectMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BootstrapTest : SpringApplicationTest() {
    @Test
    fun `admin setup creates one usable administrator when the database is empty`() {
        PostgresTestEnvironment(loadRegressionFixture = false).use { environment ->
            val database = DatabaseFactory(environment.dataSource)
            val hasher = BcryptPasswordHasher(cost = 10)
            val bootstrap = UserBootstrap(database, hasher)
            val password = PlainTextPassword("administrator-test-password")

            assertEquals(AdministratorBootstrapResult.CREATED, bootstrap.ensureAdministrator(password))
            assertEquals(
                AdministratorBootstrapResult.ALREADY_CONFIGURED,
                bootstrap.ensureAdministrator(password),
            )
            environment.connection { connection ->
                connection.createStatement().use { statement ->
                    statement
                        .executeQuery(
                            "SELECT u.password FROM g_user u JOIN g_admin_user a ON a.user_id = u.user_id " +
                                "WHERE u.cid = 'admin'",
                        ).use { rows ->
                            assertTrue(rows.next())
                            assertTrue(hasher.verify(password, PasswordHash(rows.getString(1))))
                            assertTrue(!rows.next())
                        }
                }
            }
        }
    }

    @Test
    fun `mock data is seeded only when mocking is enabled`() {
        PostgresTestEnvironment(loadRegressionFixture = false).use { environment ->
            val database = DatabaseFactory(environment.dataSource)
            val apiAccess = ApiKeyQueries(database)
            val bootstrap = MockDataBootstrap()
            val arguments = DefaultApplicationArguments()

            bootstrap
                .mockDataBootstrapRunner(
                    AppSettings(mocking = false),
                    database,
                    apiAccess,
                    CreateApiKey(database, bcryptCost = 10),
                    ObjectMapper(),
                ).run(arguments)
            assertEquals(0, database.tableRowCount("g_user"))

            bootstrap
                .mockDataBootstrapRunner(
                    AppSettings(mocking = true),
                    database,
                    apiAccess,
                    CreateApiKey(database, bcryptCost = 10),
                    ObjectMapper(),
                ).run(arguments)
            assertTrue(database.tableRowCount("g_user") > 0)
            assertEquals(3, database.tableRowCount("g_api_key"))
        }
    }
}
