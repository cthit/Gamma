package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId

class OAuthProtocolConsents(
    private val clients: OAuthClientStore,
) {
    fun isApproved(
        userId: UserId,
        clientUid: ClientUid,
    ): Boolean = clients.isApproved(userId, clientUid)

    fun approve(
        userId: UserId,
        clientUid: ClientUid,
    ) = clients.approve(userId, clientUid)

    fun revoke(
        userId: UserId,
        clientUid: ClientUid,
    ) = clients.revokeApproval(userId, clientUid)
}
