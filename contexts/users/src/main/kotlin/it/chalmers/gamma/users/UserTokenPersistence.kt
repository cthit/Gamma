package it.chalmers.gamma.users

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import java.security.MessageDigest
import java.time.Duration
import java.time.LocalDateTime
import java.util.Base64

internal val USER_LIFECYCLE_TOKEN_TTL: Duration = Duration.ofMinutes(15)

internal fun JdbcTransaction.databaseNow(): LocalDateTime =
    checkNotNull(
        exec("SELECT clock_timestamp() AT TIME ZONE 'UTC'") { result ->
            check(result.next()) { "PostgreSQL did not return its current time" }
            result.getTimestamp(1).toLocalDateTime()
        },
    )

internal fun registrationTokenMatches(token: RegistrationToken) =
    (ActivationsTable.token eq storedToken(token.value)).let { digestMatch ->
        if (token.value.startsWith(STORED_TOKEN_PREFIX_START)) {
            digestMatch
        } else {
            digestMatch or (ActivationsTable.token eq token.value)
        }
    }

internal fun passwordResetTokenMatches(token: PasswordResetToken) =
    (PasswordResetsTable.token eq storedToken(token.value)).let { digestMatch ->
        if (token.value.startsWith(STORED_TOKEN_PREFIX_START)) {
            digestMatch
        } else {
            digestMatch or (PasswordResetsTable.token eq token.value)
        }
    }

internal fun storedToken(rawToken: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(rawToken.toByteArray(Charsets.UTF_8))
    return STORED_TOKEN_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
}

private const val STORED_TOKEN_PREFIX = "{sha256-v1}"
private const val STORED_TOKEN_PREFIX_START = "{"
