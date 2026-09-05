package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction

/** Application-supplied current account authority, held until the organization's transaction ends. */
fun interface OrganizationAccess {
    /** Reject missing or locked accounts and order the authority check with promotion and demotion. */
    fun isAdministratorIn(
        transaction: JdbcTransaction,
        actor: Actor,
    ): Boolean

    fun requireAdministratorIn(
        transaction: JdbcTransaction,
        actor: Actor,
    ) {
        if (!isAdministratorIn(transaction, actor)) throw AccessDenied()
    }
}
