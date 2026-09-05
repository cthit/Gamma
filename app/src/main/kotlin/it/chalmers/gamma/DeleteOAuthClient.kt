package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.DeleteOwnedApiKeys
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.DeleteClient
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess

class DeleteOAuthClient(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val clients: DeleteClient,
    private val apiKeys: DeleteOwnedApiKeys,
) {
    fun delete(
        actor: Actor,
        uid: ClientUid,
    ): ClientOwner =
        database.commitTransaction {
            val account = accounts.requireIn(this, actor)
            val target = clients.lockIn(this, uid)
            val ownsClient = (target.owner as? ClientOwner.User)?.userId == account.userId
            if (!ownsClient && !account.isAdministrator) throw AccessDenied()
            val keyId = clients.deleteIn(this, target)
            // An earlier revocation can have removed the key already. Every remaining
            // client row and its credential must disappear together in this commit.
            if (keyId != null) apiKeys.deleteIn(this, setOf(ApiKeyId(keyId.value)))
            target.owner
        }
}
