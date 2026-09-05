package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

/** User-owned phases of the application's atomic account deletion. */
class UserDeletion(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
) {
    fun verifyPersonalDeletion(
        actor: Actor,
        password: PlainTextPassword,
    ): VerifiedAccountDeletion? {
        val user = actor as? Actor.User ?: throw AccessDenied()
        val userId = UserId(user.userId.value)
        val hash =
            database.commitTransaction(readOnly = true) {
                UsersTable
                    .select(UsersTable.password)
                    .where { UsersTable.id eq userId.value }
                    .limit(1)
                    .firstOrNull()
                    ?.get(UsersTable.password)
                    ?.let(::PasswordHash)
            }
        if (hash == null) {
            passwordHasher.verifyAgainstDummy(password)
            return null
        }
        if (!passwordHasher.verify(password, hash)) return null
        return VerifiedAccountDeletion(userId, hash)
    }

    fun lockForAdministratorDeletion(
        transaction: JdbcTransaction,
        actor: Actor,
        userId: UserId,
    ): LockedUserDeletion {
        database.requireTransaction(transaction)
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        transaction.requireAdministrator(UserId(administrator.userId.value))
        return transaction.lockDeletionTarget(userId)
    }

    fun lockForPersonalDeletion(
        transaction: JdbcTransaction,
        verified: VerifiedAccountDeletion,
    ): LockedUserDeletion {
        database.requireTransaction(transaction)
        transaction.lockAdministratorAssignments()
        val target = transaction.lockDeletionTarget(verified.userId)
        if (target.locked || target.passwordHash != verified.hash.value) {
            throw UserConflict("Credentials changed while deleting the account")
        }
        return target
    }

    fun deleteIn(
        transaction: JdbcTransaction,
        target: LockedUserDeletion,
    ) {
        database.requireTransaction(transaction)
        check(target.transaction === transaction) { "Account deletion must use the transaction that authorized it" }
        if (UsersTable.deleteWhere { UsersTable.id eq target.userId.value } != 1) {
            throw UserNotFound(USER_NOT_FOUND_MESSAGE)
        }
    }

    fun exists(userId: UserId): Boolean =
        database.commitTransaction(readOnly = true) {
            UsersTable
                .select(UsersTable.id)
                .where { UsersTable.id eq userId.value }
                .limit(1)
                .any()
        }

    private fun JdbcTransaction.lockDeletionTarget(userId: UserId): LockedUserDeletion {
        val user =
            UsersTable
                .selectAll()
                .where { UsersTable.id eq userId.value }
                .forUpdate()
                .limit(1)
                .firstOrNull()
                ?: throw UserNotFound(USER_NOT_FOUND_MESSAGE)
        requireNotFinalAdministrator(userId, "delete")
        val avatar =
            UserAvatarsTable
                .select(UserAvatarsTable.avatarUri)
                .where { UserAvatarsTable.userId eq userId.value }
                .limit(1)
                .firstOrNull()
                ?.get(UserAvatarsTable.avatarUri)
        return LockedUserDeletion(this, userId, avatar, user[UsersTable.password], user[UsersTable.locked] == true)
    }
}

class VerifiedAccountDeletion internal constructor(
    val userId: UserId,
    internal val hash: PasswordHash,
) {
    override fun toString(): String = "VerifiedAccountDeletion(<redacted>)"
}

class LockedUserDeletion internal constructor(
    internal val transaction: JdbcTransaction,
    val userId: UserId,
    val avatarUri: String?,
    internal val passwordHash: String?,
    internal val locked: Boolean,
) {
    override fun toString(): String = "LockedUserDeletion(<redacted>)"
}
