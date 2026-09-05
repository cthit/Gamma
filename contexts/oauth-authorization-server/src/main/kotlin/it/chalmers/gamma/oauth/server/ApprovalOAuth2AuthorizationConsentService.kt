package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.Scope
import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.ActorUserId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService

internal class ApprovalOAuth2AuthorizationConsentService(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val approvals: ClientApprovals,
) : OAuth2AuthorizationConsentService {
    override fun save(authorizationConsent: OAuth2AuthorizationConsent) {
        val clientUid =
            authorizationConsent.registeredClientId.toClientUidOrNull()
                ?: throw IllegalArgumentException("OAuth consent client reference is invalid")
        val userId =
            authorizationConsent.principalName.toUserIdOrNull()
                ?: throw IllegalArgumentException("OAuth consent user reference is invalid")
        val requestedScopes =
            authorizationConsent.scopes.mapTo(mutableSetOf()) { name ->
                Scope.entries.firstOrNull { it.wireValue == name }
                    ?: throw IllegalArgumentException("OAuth consent contains an unsupported scope")
            }
        require(requestedScopes.isNotEmpty()) { "OAuth consent must contain at least one scope" }
        database.commitTransaction {
            // Spring has authenticated this principal. Availability and complete scopes
            // must remain current until approval commits, including when approval already exists.
            try {
                accounts.requireIn(this, Actor.User(ActorUserId(userId.value)))
            } catch (_: AccessDenied) {
                throw IllegalArgumentException("OAuth consent user does not exist or is locked")
            }
            approvals.approveIn(this, userId, clientUid, requestedScopes)
        }
    }

    override fun remove(authorizationConsent: OAuth2AuthorizationConsent) {
        val userId = authorizationConsent.principalName.toUserIdOrNull() ?: return
        val clientUid = authorizationConsent.registeredClientId.toClientUidOrNull() ?: return
        approvals.revoke(userId, clientUid)
    }

    override fun findById(
        registeredClientId: String,
        principalName: String,
    ): OAuth2AuthorizationConsent? {
        val clientUid = registeredClientId.toClientUidOrNull() ?: return null
        val userId = principalName.toUserIdOrNull() ?: return null
        val scopes = approvals.approvedScopes(userId, clientUid) ?: return null
        val builder = OAuth2AuthorizationConsent.withId(clientUid.value.toString(), principalName)
        scopes.sortedBy(Scope::wireValue).forEach { builder.scope(it.wireValue) }
        return builder.build()
    }
}

private fun String.toUserIdOrNull(): UserId? =
    try {
        UserId.parse(this)
    } catch (_: IllegalArgumentException) {
        null
    }

private fun String.toClientUidOrNull(): ClientUid? =
    try {
        ClientUid.parse(this)
    } catch (_: IllegalArgumentException) {
        null
    }
