package it.chalmers.gamma

import it.chalmers.gamma.organization.OrganizationAccess
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.users.UserAccountAccess
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction

/** Connect organization operations to the users context without a dependency between contexts. */
class CurrentOrganizationAccess(
    private val accounts: UserAccountAccess,
) : OrganizationAccess {
    override fun isAdministratorIn(
        transaction: JdbcTransaction,
        actor: Actor,
    ): Boolean = accounts.requireIn(transaction, actor).isAdministrator
}
