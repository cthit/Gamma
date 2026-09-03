package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.OAuthProtocolConsents
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.users.UserStore
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

internal class ApprovalOAuth2AuthorizationConsentService(
    private val consents: OAuthProtocolConsents,
    private val users: UserStore,
    private val clients: RegisteredClientRepository,
) : OAuth2AuthorizationConsentService {
    override fun save(authorizationConsent: OAuth2AuthorizationConsent) {
        val reference = requireValidReference(authorizationConsent)
        val requestedScopes = authorizationConsent.scopes
        require(requestedScopes.isNotEmpty()) { "OAuth consent must contain at least one scope" }
        require(reference.registeredClient.scopes == requestedScopes) {
            "OAuth consent must cover the client's complete registered scope set"
        }

        require(users.findUser(reference.userId)?.locked == false) {
            "OAuth consent user does not exist or is locked"
        }
        consents.approve(reference.userId, reference.clientUid)
    }

    override fun remove(authorizationConsent: OAuth2AuthorizationConsent) {
        val userId = authorizationConsent.principalName.toUserIdOrNull() ?: return
        val clientUid = authorizationConsent.registeredClientId.toClientUidOrNull() ?: return
        consents.revoke(userId, clientUid)
    }

    override fun findById(
        registeredClientId: String,
        principalName: String,
    ): OAuth2AuthorizationConsent? {
        val clientUid = registeredClientId.toClientUidOrNull() ?: return null
        val userId = principalName.toUserIdOrNull() ?: return null
        val registeredClient = clients.findById(registeredClientId) ?: return null
        val approved = consents.isApproved(userId, clientUid)
        if (!approved) return null

        val builder = OAuth2AuthorizationConsent.withId(registeredClient.id, principalName)
        registeredClient.scopes.sorted().forEach(builder::scope)
        return builder.build()
    }

    private fun requireValidReference(consent: OAuth2AuthorizationConsent): ConsentReference {
        val clientUid =
            consent.registeredClientId.toClientUidOrNull()
                ?: throw IllegalArgumentException("OAuth consent client reference is invalid")
        val userId =
            consent.principalName.toUserIdOrNull()
                ?: throw IllegalArgumentException("OAuth consent user reference is invalid")
        val registeredClient =
            clients.findById(consent.registeredClientId)
                ?: throw IllegalArgumentException("OAuth consent client does not exist")
        return ConsentReference(clientUid, userId, registeredClient)
    }

    private data class ConsentReference(
        val clientUid: ClientUid,
        val userId: UserId,
        val registeredClient: RegisteredClient,
    )
}

private fun String.toUserIdOrNull(): UserId? = runCatching { UserId.parse(this) }.getOrNull()

private fun String.toClientUidOrNull(): ClientUid? = runCatching { ClientUid.parse(this) }.getOrNull()
