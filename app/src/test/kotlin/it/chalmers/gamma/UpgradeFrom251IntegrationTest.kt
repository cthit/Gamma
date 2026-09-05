package it.chalmers.gamma

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.testing.PostgresTestEnvironment
import it.chalmers.gamma.testing.RedisTestEnvironment
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.server.context.WebServerApplicationContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpgradeFrom251IntegrationTest {
    @Test
    fun `application starts on a 251 database and fixture user can log in`() {
        PostgresTestEnvironment(loadRegressionFixture = false, migrateOnStart = false).use { postgres ->
            releasedMigrations.forEach(postgres::applyClasspathSql)
            postgres.applyClasspathSql("fixtures/regression.sql")
            postgres.applyClasspathSql("fixtures/v2_5_1_persistent_state.sql")

            RedisTestEnvironment().use { redis ->
                SpringApplicationBuilder(GammaApplication::class.java)
                    .run(
                        "--server.port=0",
                        "--spring.datasource.url=${postgres.jdbcUrl}",
                        "--spring.datasource.username=${postgres.username}",
                        "--spring.datasource.password=${postgres.password}",
                        "--spring.data.redis.host=${redis.host}",
                        "--spring.data.redis.port=${redis.port}",
                        "--application.admin-setup=false",
                        "--application.mocking=false",
                        "--application.production=false",
                    ).use { context ->
                        assertEquals(
                            0,
                            context.getBean(DatabaseFactory::class.java).tableRowCount("g_client_secret_rotation"),
                        )
                        val port = requireNotNull((context as WebServerApplicationContext).webServer).port
                        val login = TestBrowser(port, uniqueAddress()).login("mscott", "password1337")
                        assertEquals(302, login.status)
                        assertTrue(login.header("Location").orEmpty().endsWith("/"))
                    }
            }
        }
    }

    private companion object {
        val releasedMigrations =
            listOf(
                "db/migration/V1__BASE.sql",
                "db/migration/V2__TOKENS.sql",
                "db/migration/V3__RESTRICT_POST_DELETION.sql",
                "db/migration/V4__ADD_POST_ORDER.sql",
                "db/migration/V5__ACCOUNT_SCAFFOLD_REQUIRES_MANAGED.sql",
            )
    }
}
