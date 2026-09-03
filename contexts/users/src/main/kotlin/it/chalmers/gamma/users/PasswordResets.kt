package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.PasswordResetToken
import it.chalmers.gamma.users.PasswordResetsTable
import it.chalmers.gamma.users.USER_LIFECYCLE_TOKEN_TTL
import it.chalmers.gamma.users.databaseNow
import it.chalmers.gamma.users.passwordResetTokenMatches
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert

class PasswordResets(
    private val database: DatabaseFactory,
    private val tokenGenerator: () -> String = ::secureUserToken,
) {
    fun findUser(token: PasswordResetToken): UserId? =
        database.transaction(readOnly = true) {
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

    fun create(userId: UserId): PasswordResetToken =
        database.transaction {
            if (!lockUserIfPresent(userId)) throw UserNotFound("User does not exist")
            createPasswordReset(userId)
        }

    fun create(
        administratorId: UserId,
        userId: UserId,
    ): PasswordResetToken =
        database.transaction {
            requireAdministrator(administratorId)
            if (!lockUserIfPresent(userId)) throw UserNotFound("User does not exist")
            createPasswordReset(userId)
        }

    fun deleteIfMatches(
        userId: UserId,
        token: PasswordResetToken,
    ): Boolean =
        database.transaction {
            PasswordResetsTable.deleteWhere {
                (PasswordResetsTable.userId eq userId.value) and passwordResetTokenMatches(token)
            } == 1
        }

    internal fun claim(token: PasswordResetToken): PasswordResetClaim? =
        database.transaction {
            val cutoff = databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
            PasswordResetsTable
                .selectAll()
                .where {
                    passwordResetTokenMatches(token) and
                        (PasswordResetsTable.createdAt greater cutoff)
                }.forUpdate()
                .limit(1)
                .firstOrNull()
                ?.let { row -> PasswordResetClaim(UserId(row[PasswordResetsTable.userId]), token) }
        }

    fun purgeExpired(): Int =
        database.transaction {
            val cutoff = databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
            PasswordResetsTable.deleteWhere { PasswordResetsTable.createdAt lessEq cutoff }
        }

    private fun org.jetbrains.exposed.v1.jdbc.JdbcTransaction.createPasswordReset(userId: UserId): PasswordResetToken {
        val token = PasswordResetToken(tokenGenerator())
        PasswordResetsTable.upsert(PasswordResetsTable.userId) {
            it[PasswordResetsTable.userId] = userId.value
            it[PasswordResetsTable.token] = token.value
            it[createdAt] = databaseNow()
        }
        return token
    }
}

internal data class PasswordResetClaim(
    val userId: UserId,
    val token: PasswordResetToken,
) {
    override fun toString(): String = "PasswordResetClaim(<redacted>)"
}
