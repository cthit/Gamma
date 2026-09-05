package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientConsentDetails
import it.chalmers.gamma.oauth.ClientId
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.DirectoryUser
import it.chalmers.gamma.users.UserQueries
import java.sql.Connection

internal class ReadOAuthConsent(
    private val database: DatabaseFactory,
    private val clients: OAuthProtocolClients,
    private val users: UserQueries,
) {
    fun read(clientId: ClientId): OAuthConsentDetails? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val client = clients.consentDetailsIn(this, clientId) ?: return@commitTransaction null
            val owner = client.owner as? ClientOwner.User
            val ownerProfile = owner?.let { users.findDirectoryUserIn(this, it.userId) }
            OAuthConsentDetails(client, ownerProfile)
        }
}

internal data class OAuthConsentDetails(
    val client: ClientConsentDetails,
    val owner: DirectoryUser?,
) {
    override fun toString(): String = "OAuthConsentDetails(<redacted>)"
}
