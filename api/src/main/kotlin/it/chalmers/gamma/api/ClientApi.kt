package it.chalmers.gamma.api

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientAuthority
import it.chalmers.gamma.oauth.OAuthApiKeyId
import it.chalmers.gamma.oauth.OAuthClient
import it.chalmers.gamma.oauth.OAuthClientStore
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.OrganizationStore
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.users.ApiUserProfile
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserStore

class ClientApi(
    private val clients: OAuthClientStore,
    private val users: UserStore,
    private val organizations: OrganizationStore,
) {
    fun groups(apiKeyId: ApiKeyId): List<Group> {
        client(apiKeyId)
        return organizations.listGroups()
    }

    fun superGroups(apiKeyId: ApiKeyId): List<SuperGroup> {
        client(apiKeyId)
        return organizations.listSuperGroups()
    }

    fun approvedUsers(apiKeyId: ApiKeyId): List<ApiUserProfile> {
        val approvedUserIds = clients.approvedUserIds(client(apiKeyId).uid).toSet()
        return users.apiUsersByIds(approvedUserIds)
    }

    fun approvedUser(
        apiKeyId: ApiKeyId,
        userId: UserId,
    ): ApiUserProfile? {
        if (userId !in clients.approvedUserIds(client(apiKeyId).uid)) return null
        return users.apiUser(userId)
    }

    fun membershipsForApprovedUser(
        apiKeyId: ApiKeyId,
        userId: UserId,
    ): List<ClientApiMembershipProjection>? {
        approvedUser(apiKeyId, userId) ?: return null
        val memberships = organizations.membershipsForUser(userId)
        val groups = organizations.groupsByIds(memberships.mapTo(mutableSetOf()) { it.groupId }).associateBy { it.id }
        val posts = organizations.postsByIds(memberships.mapTo(mutableSetOf()) { it.postId }).associateBy { it.id }
        return memberships.mapNotNull { membership ->
            val group = groups[membership.groupId] ?: return@mapNotNull null
            val post = posts[membership.postId] ?: return@mapNotNull null
            ClientApiMembershipProjection(group, post)
        }
    }

    fun authorities(apiKeyId: ApiKeyId): List<ClientAuthority> = clients.authorities(client(apiKeyId).uid)

    fun authoritiesForUser(
        apiKeyId: ApiKeyId,
        userId: UserId,
    ): List<AuthorityName> {
        val oauthClient = client(apiKeyId)
        val effectiveAuthorities = clients.authoritiesForUser(oauthClient.uid, userId).toMutableSet()
        val membershipSuperGroupIds = organizations.superGroupIdsForUser(userId).mapTo(mutableSetOf()) { it.value }
        clients
            .authorities(oauthClient.uid)
            .filter { authority -> authority.superGroupIds.any { it in membershipSuperGroupIds } }
            .forEach { effectiveAuthorities += it.name }
        return effectiveAuthorities.sortedBy { it.value }
    }

    private fun client(apiKeyId: ApiKeyId): OAuthClient =
        clients.findClientByApiKey(OAuthApiKeyId(apiKeyId.value))
            ?: throw it.chalmers.gamma.platform.core
                .AccessDenied()
}

data class ClientApiMembershipProjection(
    val group: Group,
    val post: Post,
)
