package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.selectAll

/** Resolves an unexpired recovery token for the password-reset page. */
class PasswordResets(
    private val database: DatabaseFactory,
) {
    fun findUser(token: PasswordResetToken): UserId? =
        database.commitTransaction(readOnly = true) {
            val cutoff = databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
            PasswordResetsTable
                .selectAll()
                .where {
                    passwordResetTokenMatches(token) and
                        (PasswordResetsTable.createdAt greater cutoff)
                }.limit(1)
                .firstOrNull()
                ?.get(PasswordResetsTable.userId)
                ?.let(::UserId)
        }
}
