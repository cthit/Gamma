package it.chalmers.gamma.oauth

import java.util.UUID

class OAuthProtocolClients(
    private val clients: OAuthClientStore,
) {
    fun serverClient(uid: ClientUid): OAuthServerClient? = clients.serverClient(uid)

    fun serverClient(clientId: ClientId): OAuthServerClient? = clients.serverClient(clientId)

    fun restrictedSuperGroupIds(uid: ClientUid): Set<UUID> = clients.findClient(uid)?.restrictedSuperGroupIds.orEmpty()
}
