package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

data class PasswordResetCompletion(
    val token: PasswordResetToken,
    val password: PlainTextPassword,
    val confirmedPassword: String,
) {
    override fun toString(): String = "PasswordResetCompletion(<redacted>)"
}

class ResetPassword(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
) {
    // Keep token binding, hashing, locked revalidation, and atomic consumption together as one reset story.
    @Suppress("LongMethod")
    fun reset(
        actor: Actor,
        input: PasswordResetCompletion,
    ) {
        if (actor != Actor.Anonymous) throw AccessDenied()
        if (input.password.value != input.confirmedPassword) throw UserConflict("Password was not confirmed")
        val credential =
            database.commitTransaction(readOnly = true) {
                val userId =
                    PasswordResetsTable
                        .selectAll()
                        .where {
                            passwordResetTokenMatches(input.token) and
                                (PasswordResetsTable.createdAt greater databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL))
                        }.limit(1)
                        .firstOrNull()
                        ?.get(PasswordResetsTable.userId)
                        ?: throw UserConflict("Password reset token is invalid or expired")
                val user =
                    UsersTable
                        .select(UsersTable.version)
                        .where { UsersTable.id eq userId }
                        .limit(1)
                        .firstOrNull()
                        ?: throw UserNotFound(USER_NOT_FOUND_MESSAGE)
                ResetCredential(UserId(userId), user[UsersTable.version] ?: 0)
            }

        val passwordHash = passwordHasher.hash(input.password)
        database.commitTransaction {
            // Issuance locks the user before replacing the reset row. Follow that order here too.
            val credentialIsCurrent =
                UsersTable
                    .select(UsersTable.version)
                    .where {
                        (UsersTable.id eq credential.userId.value) and
                            UsersTable.version.matchesStoredVersion(credential.version)
                    }.forUpdate()
                    .limit(1)
                    .any()
            if (!credentialIsCurrent) throw UserConflict("User is missing or changed while setting the password")
            val token =
                PasswordResetsTable
                    .selectAll()
                    .where {
                        (PasswordResetsTable.userId eq credential.userId.value) and
                            passwordResetTokenMatches(input.token)
                    }.forUpdate()
                    .limit(1)
                    .firstOrNull()
            // A lock wait must not extend token validity. Read the actual clock after both locks are held.
            if (token == null ||
                token[PasswordResetsTable.createdAt] <= databaseNow().minus(USER_LIFECYCLE_TOKEN_TTL)
            ) {
                throw UserConflict("Password reset token is invalid or expired")
            }
            val consumed =
                PasswordResetsTable.deleteWhere {
                    (PasswordResetsTable.userId eq credential.userId.value) and passwordResetTokenMatches(input.token)
                }
            check(consumed == 1) { "Locked password reset token disappeared before consumption" }
            val changed =
                UsersTable.update({ UsersTable.id eq credential.userId.value }) {
                    it[password] = passwordHash.value
                    it[version] = credential.version + 1
                    it[updatedAt] = userPersistenceTime()
                }
            check(changed == 1) { "Locked user disappeared before password reset completed" }
        }
    }
}

private data class ResetCredential(
    val userId: UserId,
    val version: Int,
) {
    override fun toString(): String = "ResetCredential(<redacted>)"
}
