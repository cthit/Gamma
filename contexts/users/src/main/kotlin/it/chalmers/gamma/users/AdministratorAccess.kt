package it.chalmers.gamma.users

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction

/** Current user authority for an application operation composing multiple contexts. */
class AdministratorAccess(
    private val database: DatabaseFactory,
) {
    fun requireIn(
        transaction: JdbcTransaction,
        actor: Actor,
    ) {
        database.requireTransaction(transaction)
        val user = actor as? Actor.User ?: throw AccessDenied()
        // The shared assignment lock stays held through the caller's commit, ordering
        // the authorized action with administrator promotion or demotion.
        transaction.requireAdministratorForRead(UserId(user.userId.value))
    }
}
