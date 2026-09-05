package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientId
import it.chalmers.gamma.oauth.ClientOwner
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.OAuthServerClient
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings

internal class GammaRegisteredClientRepository(
    private val clients: OAuthProtocolClients,
) : RegisteredClientRepository {
    override fun save(registeredClient: RegisteredClient): Unit =
        throw UnsupportedOperationException("Clients are managed by the OAuth bounded context")

    override fun findById(id: String): RegisteredClient? =
        runCatching { ClientUid.parse(id) }.getOrNull()?.let { clients.serverClient(it) }?.toSpringClient()

    override fun findByClientId(clientId: String): RegisteredClient? =
        runCatching { ClientId(clientId) }.getOrNull()?.let { clients.serverClient(it) }?.toSpringClient()

    private fun OAuthServerClient.toSpringClient(): RegisteredClient {
        val source = client
        val builder =
            RegisteredClient
                .withId(source.uid.value.toString())
                .clientId(source.clientId.value)
                .clientSecret(encodedSecret)
                .clientName(source.name.value)
                .redirectUri(source.redirectUri.value)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientSettings(
                    ClientSettings
                        .builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(false)
                        .setting("gamma.is-official", source.owner is ClientOwner.Official)
                        .build(),
                )
        source.scopes.forEach { builder.scope(it.wireValue) }
        return builder.build()
    }
}
