package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess
import it.chalmers.gamma.users.UserProfile
import it.chalmers.gamma.users.UserQueries
import java.sql.Connection

class ReadOAuthClientLists(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val clients: OAuthClientQueries,
    private val users: UserQueries,
) {
    // These reads lock current account authority and materialize their complete page snapshot.
    fun officialClients(actor: Actor): List<OAuthClient> =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            if (!account.isAdministrator) throw AccessDenied()
            clients.listClientsIn(this).filter { it.owner is ClientOwner.Official }
        }

    fun myClients(actor: Actor): List<OAuthClient> =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            clients.listClientsIn(this, account.userId)
        }

    fun personalClientsForAdministration(actor: Actor): List<PersonalOAuthClient> =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            if (!account.isAdministrator) throw AccessDenied()
            val personal = clients.listClientsIn(this).filter { it.owner is ClientOwner.User }
            val ownerIds = personal.map { (it.owner as ClientOwner.User).userId }.toSet()
            val owners = users.usersByIdsIn(this, ownerIds).associateBy { it.id }
            personal.map { client -> PersonalOAuthClient(client, owners[(client.owner as ClientOwner.User).userId]) }
        }

    fun approvedClients(actor: Actor): List<OAuthClient> =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            clients.approvedClientsIn(this, account.userId)
        }
}

data class PersonalOAuthClient(
    val client: OAuthClient,
    val owner: UserProfile?,
) {
    override fun toString(): String = "PersonalOAuthClient(<redacted>)"
}
