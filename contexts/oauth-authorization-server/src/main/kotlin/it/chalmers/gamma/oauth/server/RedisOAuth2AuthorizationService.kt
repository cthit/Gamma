package it.chalmers.gamma.oauth.server

import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import java.time.Clock
import java.time.Instant

internal class RedisOAuth2AuthorizationService(
    private val store: RedisOAuthAuthorizationStore,
    private val clients: RegisteredClientRepository,
    private val currentAuthenticationTime: () -> Instant? = { null },
    private val currentClientId: () -> String? = { null },
    private val clock: Clock = Clock.systemUTC(),
) : OAuth2AuthorizationService {
    override fun save(authorization: OAuth2Authorization) {
        val authorizationToSave =
            authorization.withCurrentAuthenticationTime(
                currentAuthenticationTime(),
            )
        val registeredClient = requireCurrentRegisteredClient(authorizationToSave)
        val expectedRevision = authorizationToSave.getAttribute<String>(STORAGE_REVISION_ATTRIBUTE)
        store.save(authorizationToSave.toStored(registeredClient, clock), expectedRevision)
    }

    private fun requireCurrentRegisteredClient(authorization: OAuth2Authorization): RegisteredClient =
        clients.findById(authorization.registeredClientId)
            ?: throw OAuthRegisteredClientRevoked()

    override fun remove(authorization: OAuth2Authorization) {
        store.removeById(authorization.id)
    }

    override fun findById(id: String): OAuth2Authorization? = store.findById(id)?.toSpringAuthorization(clients)

    override fun findByToken(
        token: String,
        tokenType: OAuth2TokenType?,
    ): OAuth2Authorization? {
        if (token.isBlank()) return null
        val kinds =
            if (tokenType == null) {
                OAuthAuthorizationIndexKind.entries
            } else {
                listOfNotNull(tokenType.toIndexKind())
            }
        val stateClientId = currentClientId()
        for (kind in kinds) {
            val authorization =
                store
                    .findByIndex(
                        kind,
                        token,
                        stateClientId.takeIf { kind == OAuthAuthorizationIndexKind.STATE },
                    )?.toSpringAuthorization(clients)
            if (authorization != null) {
                if (!authorization.matches(kind, token)) {
                    throw OAuthAuthorizationStorageFailure("OAuth authorization lookup is inconsistent")
                }
                return authorization
            }
        }
        return null
    }
}

private fun OAuth2Authorization.withCurrentAuthenticationTime(current: Instant?): OAuth2Authorization {
    if (current == null || getAttribute<Instant>(OAUTH_AUTHENTICATION_TIME_ATTRIBUTE) != null) return this
    return OAuth2Authorization
        .from(this)
        .attribute(OAUTH_AUTHENTICATION_TIME_ATTRIBUTE, current)
        .build()
}

internal class OAuthRegisteredClientRevoked :
    OAuthAuthorizationStorageFailure("OAuth registered client no longer exists")
