package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert
import java.time.Year

data class IssuedPasswordReset(
    val user: UserDetails,
    val token: PasswordResetToken,
) {
    override fun toString(): String = "IssuedPasswordReset(<redacted>)"
}

class CreatePasswordReset(
    private val database: DatabaseFactory,
    private val tokenGenerator: () -> String = ::secureUserToken,
) {
    fun create(
        actor: Actor,
        userId: UserId,
    ): IssuedPasswordReset {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        // Allocate once so retries persist the same token. Authority to issue it is checked under the lock below.
        val token = PasswordResetToken(tokenGenerator())
        return database.commitTransaction {
            requireAdministrator(UserId(administrator.userId.value))
            val user =
                UsersTable
                    .selectAll()
                    .where { UsersTable.id eq userId.value }
                    .forUpdate()
                    .limit(1)
                    .firstOrNull()
                    ?: throw UserNotFound(USER_NOT_FOUND_MESSAGE)
            val details =
                UserDetails(
                    userId,
                    Cid(user[UsersTable.cid]),
                    Nick(user[UsersTable.nick]),
                    FirstName(user[UsersTable.firstName]),
                    LastName(user[UsersTable.lastName]),
                    AcceptanceYear.of(
                        checkNotNull(user[UsersTable.acceptanceYear]) { "User acceptance year is missing" },
                        Year.now().value,
                    ),
                    user[UsersTable.version] ?: 0,
                )
            // Match completion's user-before-token lock order and retain the token format used by Gamma 2.5.1.
            PasswordResetsTable.upsert(PasswordResetsTable.userId) {
                it[PasswordResetsTable.userId] = userId.value
                it[PasswordResetsTable.token] = token.value
                it[createdAt] = databaseNow()
            }
            IssuedPasswordReset(details, token)
        }
    }
}
