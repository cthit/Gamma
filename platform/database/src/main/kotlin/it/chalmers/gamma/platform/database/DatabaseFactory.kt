package it.chalmers.gamma.platform.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.util.PropertyElf
import org.jetbrains.exposed.v1.core.Key
import org.jetbrains.exposed.v1.core.statements.StatementInterceptor
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction as exposedTransaction
import java.sql.Connection
import java.sql.SQLException
import java.util.Properties
import javax.sql.DataSource

class DatabaseFactory private constructor(
    resource: DatabaseResource,
    private val statementInterceptors: List<StatementInterceptor>,
) : AutoCloseable {
    private val ownedConnectionPool = resource.ownedConnectionPool
    internal val dataSource: DataSource = resource.dataSource
    private val database = Database.connect(dataSource)
    private val registeredInterceptorsKey = Key<Unit>()
    private val transactionDefaults =
        try {
            dataSource.connection.use { connection ->
                TransactionDefaults(
                    isolationLevel = connection.transactionIsolation,
                    readOnly = connection.isReadOnly,
                )
            }
        } catch (failure: SQLException) {
            ownedConnectionPool?.close()
            throw failure
        }

    constructor(settings: DatabaseSettings) : this(createOwnedDatabaseResource(settings), emptyList())

    /** Uses a Spring-managed pool after Spring Boot's Flyway initializer has completed. */
    constructor(
        dataSource: DataSource,
        statementInterceptors: List<StatementInterceptor> = emptyList(),
    ) : this(DatabaseResource(dataSource, null), statementInterceptors)

    fun ping(): Boolean =
        databaseIsReachable {
            dataSource.connection.use { connection ->
                connection.prepareStatement("SELECT 1").use { statement ->
                    statement.queryTimeout = 1
                    statement.executeQuery().use { result -> result.next() && result.getInt(1) == 1 }
                }
            }
        }

    /**
     * Runs [statement] in a database transaction.
     *
     * Exposed can execute [statement] up to three times when an attempt throws [SQLException].
     * Transaction bodies must therefore be retry-safe and must not perform non-idempotent external effects.
     */
    fun <T> transaction(
        readOnly: Boolean = transactionDefaults.readOnly,
        isolationLevel: Int = transactionDefaults.isolationLevel,
        statement: JdbcTransaction.() -> T,
    ): T =
        exposedTransaction(
            db = database,
            transactionIsolation = isolationLevel,
            readOnly = readOnly,
        ) {
            getOrCreate(registeredInterceptorsKey) {
                statementInterceptors.forEach(::registerInterceptor)
            }
            statement()
        }

    fun isTableEmpty(tableName: String): Boolean {
        require(tableName.matches(Regex("^[a-z][a-z0-9_]*$"))) { "Invalid table name" }
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT NOT EXISTS (SELECT 1 FROM $tableName)").use { result ->
                    check(result.next())
                    return result.getBoolean(1)
                }
            }
        }
    }

    fun tableRowCount(tableName: String): Long {
        require(tableName.matches(Regex("^[a-z][a-z0-9_]*$"))) { "Invalid table name" }
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT COUNT(*) FROM $tableName").use { result ->
                    check(result.next())
                    return result.getLong(1)
                }
            }
        }
    }

    fun executeSqlScript(script: String) {
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            connection.createStatement().use { statement -> statement.execute(script) }
            connection.commit()
        }
    }

    override fun close() {
        ownedConnectionPool?.close()
    }

    private data class TransactionDefaults(
        val isolationLevel: Int,
        val readOnly: Boolean,
    )
}

private data class DatabaseResource(
    val dataSource: DataSource,
    val ownedConnectionPool: HikariDataSource?,
)

private fun createOwnedDatabaseResource(settings: DatabaseSettings): DatabaseResource {
    val connectionPool =
        HikariDataSource(
            HikariConfig().apply {
                PropertyElf.setTargetFromProperties(
                    this,
                    Properties().apply {
                        settings.hikariProperties.forEach(::setProperty)
                    },
                )
                jdbcUrl = settings.jdbcUrl
                username = settings.username
                password = settings.password
                driverClassName = "org.postgresql.Driver"
                maximumPoolSize = settings.maximumPoolSize
                minimumIdle = settings.minimumIdle
                connectionTimeout = settings.connectionTimeout.toMillis()
                maxLifetime = settings.maximumLifetime.toMillis()
                idleTimeout = settings.idleTimeout.toMillis()
                keepaliveTime = settings.keepaliveTime.toMillis()
                validationTimeout = settings.validationTimeout.toMillis()
                initializationFailTimeout = settings.initializationFailTimeout.toMillis()
                isReadOnly = settings.readOnly
                isAutoCommit = settings.autoCommit
                transactionIsolation = settings.transactionIsolation
                addDataSourceProperty("tcpKeepAlive", "true")
                settings.dataSourceProperties.forEach(::addDataSourceProperty)
                validate()
            },
        )
    return DatabaseResource(connectionPool, connectionPool)
}

internal fun databaseIsReachable(checkConnection: () -> Boolean): Boolean =
    try {
        checkConnection()
    } catch (_: SQLException) {
        false
    }
