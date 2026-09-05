package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

class UserAuthentication(
    private val database: DatabaseFactory,
    private val passwordHasher: PasswordHasher,
) {
    fun authenticate(
        identifier: UserIdentifier,
        password: PlainTextPassword,
    ): AuthenticatedUser? {
        val identity =
            when (identifier) {
                is UserId -> UsersTable.id eq identifier.value
                is Cid -> UsersTable.cid eq identifier.value
                is Email -> UsersTable.email.lowerCase() eq identifier.value.lowercase()
                else -> error("Unsupported user identifier type")
            }
        val credential =
            database.commitTransaction(readOnly = true) {
                UsersTable
                    .select(UsersTable.id, UsersTable.password)
                    .where { identity }
                    .limit(1)
                    .firstOrNull()
                    ?.let { row ->
                        LoginCredential(UserId(row[UsersTable.id]), row[UsersTable.password]?.let(::PasswordHash))
                    }
            }

        // Hashing neither holds a connection nor repeats when a database read retries.
        val hash = credential?.hash
        if (hash == null) {
            passwordHasher.verifyAgainstDummy(password)
            return null
        }
        if (!passwordHasher.verify(password, hash)) return null

        return database.commitTransaction(readOnly = true) {
            // Bind acceptance to the verified credential and submitted identifier. Load current
            // account status and authority in this same statement, including changes made while hashing.
            val user =
                UsersTable
                    .join(
                        otherTable = AdminUsersTable,
                        joinType = JoinType.LEFT,
                        onColumn = UsersTable.id,
                        otherColumn = AdminUsersTable.userId,
                    ).selectAll()
                    .where { (UsersTable.id eq credential.userId.value) and identity }
                    .limit(1)
                    .firstOrNull()
                    ?: return@commitTransaction null
            if (user[UsersTable.password] != hash.value || user[UsersTable.locked] == true) {
                return@commitTransaction null
            }
            AuthenticatedUser(
                credential.userId,
                Nick(user[UsersTable.nick]),
                administrator = user.getOrNull(AdminUsersTable.userId) != null,
            )
        }
    }

    fun sessionAccess(userId: UserId): SessionAccess? =
        database.commitTransaction(readOnly = true) {
            UsersTable
                .join(
                    otherTable = AdminUsersTable,
                    joinType = JoinType.LEFT,
                    onColumn = UsersTable.id,
                    otherColumn = AdminUsersTable.userId,
                ).selectAll()
                .where { UsersTable.id eq userId.value }
                .limit(1)
                .firstOrNull()
                ?.let { row ->
                    SessionAccess(
                        locked = row[UsersTable.locked] == true,
                        administrator = row.getOrNull(AdminUsersTable.userId) != null,
                    )
                }
        }
}

data class AuthenticatedUser(
    val userId: UserId,
    val nick: Nick,
    val administrator: Boolean,
) {
    override fun toString(): String = "AuthenticatedUser(<redacted>)"
}

data class SessionAccess(
    val locked: Boolean,
    val administrator: Boolean,
)

private data class LoginCredential(
    val userId: UserId,
    val hash: PasswordHash?,
) {
    override fun toString(): String = "LoginCredential(<redacted>)"
}
