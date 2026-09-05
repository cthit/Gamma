package it.chalmers.gamma

import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientAuthorities
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess

class DeleteOAuthClientAuthority(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val authorities: ClientAuthorities,
) {
    fun delete(
        actor: Actor,
        uid: ClientUid,
        name: AuthorityName,
    ) {
        database.commitTransaction {
            val account = accounts.requireIn(this, actor)
            val target = authorities.lockIn(this, uid)
            val ownsClient = (target.owner as? ClientOwner.User)?.userId == account.userId
            if (!ownsClient && !account.isAdministrator) throw AccessDenied()
            authorities.deleteIn(this, target, name)
        }
    }
}
