package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.select

/** Current account identity and authority for an application operation spanning contexts. */
class UserAccountAccess(
    private val database: DatabaseFactory,
) {
    fun requireIn(
        transaction: JdbcTransaction,
        actor: Actor,
    ): CurrentUserAccount {
        database.requireTransaction(transaction)
        val user = actor as? Actor.User ?: throw AccessDenied()
        // Match user deletion's order: administrator assignments, then the account, then owned records.
        transaction.exec("LOCK TABLE g_admin_user IN SHARE MODE")
        val row =
            UsersTable
                .select(UsersTable.locked)
                .where { UsersTable.id eq user.userId.value }
                .forUpdate()
                .firstOrNull()
                ?: throw AccessDenied()
        if (row[UsersTable.locked] == true) throw AccessDenied()
        val administrator =
            AdminUsersTable
                .select(AdminUsersTable.userId)
                .where {
                    AdminUsersTable.userId eq
                        user.userId.value
                }.any()
        return CurrentUserAccount(UserId(user.userId.value), administrator)
    }
}

class CurrentUserAccount internal constructor(
    val userId: UserId,
    val isAdministrator: Boolean,
) {
    override fun toString(): String = "CurrentUserAccount(<redacted>)"
}
