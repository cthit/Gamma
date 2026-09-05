package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.DatabaseSettings
import it.chalmers.gamma.testing.PostgresTestEnvironment
import java.time.Duration

internal fun withUserDatabase(
    maximumPoolSize: Int = 4,
    loadRegressionFixture: Boolean = true,
    block: (DatabaseFactory) -> Unit,
) {
    withUserPostgresEnvironment(loadRegressionFixture) { postgres ->
        identityDatabase(postgres, maximumPoolSize).use(block)
    }
}

internal fun withTwoUserDatabases(
    maximumPoolSize: Int = 2,
    loadRegressionFixture: Boolean = true,
    block: (DatabaseFactory, DatabaseFactory) -> Unit,
) {
    withUserPostgresEnvironment(loadRegressionFixture) { postgres ->
        identityDatabase(postgres, maximumPoolSize).use { first ->
            identityDatabase(postgres, maximumPoolSize).use { second -> block(first, second) }
        }
    }
}

private fun withUserPostgresEnvironment(
    loadRegressionFixture: Boolean,
    block: (PostgresTestEnvironment) -> Unit,
) {
    PostgresTestEnvironment(
        loadRegressionFixture = loadRegressionFixture,
    ).use(block)
}

private fun identityDatabase(
    postgres: PostgresTestEnvironment,
    maximumPoolSize: Int,
) = DatabaseFactory(
    DatabaseSettings(
        jdbcUrl = postgres.jdbcUrl,
        username = postgres.username,
        password = postgres.password,
        maximumPoolSize = maximumPoolSize,
        minimumIdle = 0,
        connectionTimeout = Duration.ofMillis(500),
    ),
)
