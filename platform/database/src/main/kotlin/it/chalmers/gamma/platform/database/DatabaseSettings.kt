package it.chalmers.gamma.platform.database

import java.time.Duration

data class DatabaseSettings(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val maximumPoolSize: Int = 10,
    val minimumIdle: Int = maximumPoolSize,
    val connectionTimeout: Duration = Duration.ofSeconds(30),
    val maximumLifetime: Duration = Duration.ofMinutes(30),
    val idleTimeout: Duration = Duration.ofMinutes(10),
    val keepaliveTime: Duration = Duration.ZERO,
    val validationTimeout: Duration = Duration.ofSeconds(5),
    val initializationFailTimeout: Duration = Duration.ofMillis(1),
    val readOnly: Boolean = false,
    val autoCommit: Boolean = true,
    val transactionIsolation: String = "TRANSACTION_READ_COMMITTED",
    val hikariProperties: Map<String, String> = emptyMap(),
    val dataSourceProperties: Map<String, String> = emptyMap(),
) {
    override fun toString(): String =
        "DatabaseSettings(" +
            "jdbcUrl=${jdbcUrl.withRedactedParameters()}, " +
            "username=<redacted>, password=<redacted>, maximumPoolSize=$maximumPoolSize, " +
            "minimumIdle=$minimumIdle, connectionTimeout=$connectionTimeout, " +
            "maximumLifetime=$maximumLifetime, idleTimeout=$idleTimeout, keepaliveTime=$keepaliveTime, " +
            "validationTimeout=$validationTimeout, initializationFailTimeout=$initializationFailTimeout, " +
            "readOnly=$readOnly, autoCommit=$autoCommit, transactionIsolation=$transactionIsolation, " +
            "hikariProperties=${hikariProperties.size}, dataSourceProperties=${dataSourceProperties.size})"

    init {
        requirePostgreSqlJdbcUrl(jdbcUrl, "DB_URL")
        require(maximumPoolSize > 0) { "Database pool size must be positive" }
        require(minimumIdle in 0..maximumPoolSize) {
            "Database minimum idle connections must be between zero and the maximum pool size"
        }
        require(connectionTimeout.toMillis() >= 250) {
            "Database connection timeout must be at least 250 milliseconds"
        }
        require(maximumLifetime.isZero || (!maximumLifetime.isNegative && maximumLifetime.toMillis() >= 30_000)) {
            "Database maximum lifetime must be zero or at least 30 seconds"
        }
        require(!idleTimeout.isNegative) { "Database idle timeout must not be negative" }
        require(keepaliveTime.isZero || (!keepaliveTime.isNegative && keepaliveTime.toMillis() >= 30_000)) {
            "Database keepalive time must be zero or at least 30 seconds"
        }
        require(validationTimeout.toMillis() >= 250) {
            "Database validation timeout must be at least 250 milliseconds"
        }
        require(transactionIsolation.isNotBlank() && transactionIsolation.none(Char::isISOControl)) {
            "Database transaction isolation must not be blank"
        }
        requireValidDataSourcePropertyNames(dataSourceProperties)
    }
}

private fun requirePostgreSqlJdbcUrl(
    jdbcUrl: String,
    settingName: String,
) {
    require(jdbcUrl.startsWith(POSTGRESQL_JDBC_PREFIX) && jdbcUrl.none(Char::isISOControl)) {
        "$settingName must be a PostgreSQL JDBC URL"
    }
    val connectionTarget = jdbcUrl.removePrefix(POSTGRESQL_JDBC_PREFIX).substringBefore('?').substringBefore('#')
    require(connectionTarget.isNotBlank() && connectionTarget != "//") {
        "$settingName must contain a PostgreSQL database or host"
    }
    if (connectionTarget.startsWith("//")) {
        val authority = connectionTarget.removePrefix("//").substringBefore('/')
        require(authority.isNotBlank() && authority.split(',').none(String::isBlank)) {
            "$settingName must contain only non-blank PostgreSQL hosts"
        }
    }
}

private fun requireValidDataSourcePropertyNames(dataSourceProperties: Map<String, String>) {
    require(dataSourceProperties.keys.all { key -> key.isNotBlank() && key.none(Char::isISOControl) }) {
        "Database data source property names must not be blank or contain control characters"
    }
}

private const val POSTGRESQL_JDBC_PREFIX = "jdbc:postgresql:"

private fun String.withRedactedParameters(): String {
    val connectionIdentity = substringBefore('?').substringBefore('#')
    val authorityStart = connectionIdentity.indexOf("://").takeIf { it >= 0 }?.plus(3)
    val redactedIdentity =
        if (authorityStart == null) {
            connectionIdentity
        } else {
            val authorityEnd =
                connectionIdentity.indexOf('/', authorityStart).takeIf { it >= 0 } ?: connectionIdentity.length
            val credentialEnd = connectionIdentity.lastIndexOf('@', authorityEnd - 1)
            if (credentialEnd >= authorityStart) {
                connectionIdentity.replaceRange(authorityStart, credentialEnd + 1, "<redacted>@")
            } else {
                connectionIdentity
            }
        }
    return if (connectionIdentity == this) redactedIdentity else "$redactedIdentity?<redacted>"
}
