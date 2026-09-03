package it.chalmers.gamma.testing

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.testcontainers.postgresql.PostgreSQLContainer
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection

// This environment owns its pool until close(); Flyway exposes migration locations only as a Java vararg.
// The long public constructor is retained for source, binary, and reflection compatibility with existing tests.
@Suppress("LongParameterList", "MissingUseCall", "SpreadOperator")
class PostgresTestEnvironment(
    migrationLocations: List<String> = listOf("classpath:db/migration"),
    loadRegressionFixture: Boolean = true,
    migrationTarget: String? = null,
    preMigrationSqlResources: List<String> = emptyList(),
    preMigrationSqlFiles: List<Path> = emptyList(),
    baselineVersion: String? = null,
    migrateOnStart: Boolean = true,
    postgresImage: String =
        "postgres@sha256:e17e86066e5ef83e0952a9347f5c792b7ece00972e2aa787a6986f471b3dd3d5",
) : AutoCloseable {
    private val configuration =
        PostgresTestEnvironmentConfiguration(
            migrationLocations,
            loadRegressionFixture,
            migrationTarget,
            preMigrationSqlResources,
            preMigrationSqlFiles,
            baselineVersion,
            migrateOnStart,
            postgresImage,
        )
    private val container = PostgreSQLContainer(configuration.postgresImage)

    val dataSource: HikariDataSource
    val jdbcUrl: String get() = container.jdbcUrl
    val username: String get() = container.username
    val password: String get() = container.password

    init {
        dataSource =
            OwnedResources.acquire(
                container,
                { it.start() },
                ::createDataSource,
                { pool -> initializeDatabase(pool, configuration, javaClass.classLoader) },
            )
    }

    fun <T> connection(block: (Connection) -> T): T = dataSource.connection.use(block)

    fun applyClasspathSql(resourceName: String) {
        applyClasspathSql(dataSource, javaClass.classLoader, resourceName)
    }

    fun migrate(migrationLocations: List<String>) {
        Flyway
            .configure()
            .dataSource(dataSource)
            .locations(*migrationLocations.toTypedArray())
            .cleanDisabled(true)
            .load()
            .migrate()
    }

    override fun close() = OwnedResources.close(dataSource, container)

    private fun createDataSource(container: PostgreSQLContainer): HikariDataSource =
        HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = container.jdbcUrl
                username = container.username
                password = container.password
                maximumPoolSize = 4
                minimumIdle = 0
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_READ_COMMITTED"
                validate()
            },
        )

    private fun initializeDatabase(
        dataSource: HikariDataSource,
        configuration: PostgresTestEnvironmentConfiguration,
        classLoader: ClassLoader,
    ) {
        configuration.preMigrationSqlResources.forEach { resourceName ->
            applyClasspathSql(dataSource, classLoader, resourceName)
        }
        configuration.preMigrationSqlFiles.forEach { path -> applySql(dataSource, Files.readString(path)) }

        if (configuration.migrateOnStart) {
            val flywayConfiguration =
                Flyway
                    .configure()
                    .dataSource(dataSource)
                    .locations(*configuration.migrationLocations.toTypedArray())
                    .cleanDisabled(true)

            configuration.migrationTarget?.let(flywayConfiguration::target)
            configuration.baselineVersion?.let { baselineVersion ->
                flywayConfiguration
                    .baselineOnMigrate(true)
                    .baselineVersion(baselineVersion)
            }
            flywayConfiguration.load().migrate()
        }

        if (configuration.loadRegressionFixture && configuration.migrateOnStart) {
            applyClasspathSql(dataSource, classLoader, "fixtures/regression.sql")
        }
    }

    private fun applyClasspathSql(
        dataSource: HikariDataSource,
        classLoader: ClassLoader,
        resourceName: String,
    ) {
        val sql =
            checkNotNull(classLoader.getResource(resourceName)) {
                "Missing SQL fixture: $resourceName"
            }.readText()
        applySql(dataSource, sql)
    }

    private fun applySql(
        dataSource: HikariDataSource,
        sql: String,
    ) {
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement -> statement.execute(sql) }
            connection.commit()
        }
    }
}

private data class PostgresTestEnvironmentConfiguration(
    val migrationLocations: List<String>,
    val loadRegressionFixture: Boolean,
    val migrationTarget: String?,
    val preMigrationSqlResources: List<String>,
    val preMigrationSqlFiles: List<Path>,
    val baselineVersion: String?,
    val migrateOnStart: Boolean,
    val postgresImage: String,
)
