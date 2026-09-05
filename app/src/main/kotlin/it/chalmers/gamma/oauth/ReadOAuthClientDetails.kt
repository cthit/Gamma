package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess
import java.sql.Connection

class ReadOAuthClientDetails(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val clients: OAuthClientQueries,
) {
    fun read(
        actor: Actor,
        uid: ClientUid,
    ): OAuthClientDetails =
        // Account authorization takes locks, so this snapshot cannot be a read-only transaction.
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            val client = clients.findClientIn(this, uid) ?: throw OAuthClientNotFound("Client does not exist")
            val ownsClient = (client.owner as? ClientOwner.User)?.userId == account.userId
            if (!ownsClient && !account.isAdministrator) throw AccessDenied()
            OAuthClientDetails(client, clients.authoritiesIn(this, uid))
        }
}

data class OAuthClientDetails(
    val client: OAuthClient,
    val authorities: List<ClientAuthority>,
) {
    override fun toString(): String = "OAuthClientDetails(<redacted>)"
}
