package it.chalmers.gamma.users

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll

// Tests inspect the persisted credential and version without expanding the production query API.
internal fun DatabaseFactory.findPasswordUser(userId: UserId): PasswordUser? =
    commitTransaction(readOnly = true) {
        UsersTable
            .selectAll()
            .where { UsersTable.id eq userId.value }
            .limit(1)
            .firstOrNull()
            ?.let { user ->
                PasswordUser(
                    cid = user[UsersTable.cid],
                    nick = user[UsersTable.nick],
                    emailLocalPart = user[UsersTable.email].substringBefore('@'),
                    passwordHash = user[UsersTable.password]?.let(::PasswordHash),
                    version = user[UsersTable.version] ?: 0,
                )
            }
    }

internal data class PasswordUser(
    val cid: String,
    val nick: String,
    val emailLocalPart: String,
    val passwordHash: PasswordHash?,
    val version: Int,
) {
    override fun toString(): String = "PasswordUser(<redacted>)"
}
