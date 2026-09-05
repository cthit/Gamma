package it.chalmers.gamma.api

import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.oauth.AuthorityName
import it.chalmers.gamma.oauth.ClientAuthority
import it.chalmers.gamma.oauth.OAuthApiKeyId
import it.chalmers.gamma.oauth.OAuthClient
import it.chalmers.gamma.oauth.OAuthClientQueries
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.ApiUserProfile
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import java.sql.Connection

class ClientApi(
    private val database: DatabaseFactory,
    private val clients: OAuthClientQueries,
    private val users: UserQueries,
    private val organizations: OrganizationQueries,
) {
    fun groups(apiKeyId: ApiKeyId): List<Group> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            client(this, apiKeyId)
            return@commitTransaction organizations.listGroupsIn(this)
        }

    fun superGroups(apiKeyId: ApiKeyId): List<SuperGroup> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            client(this, apiKeyId)
            return@commitTransaction organizations.listSuperGroupsIn(this)
        }

    fun approvedUsers(apiKeyId: ApiKeyId): List<ApiUserProfile> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val approvedUserIds = clients.approvedUserIdsIn(this, client(this, apiKeyId).uid).toSet()
            return@commitTransaction users.apiUsersByIdsIn(this, approvedUserIds)
        }

    fun approvedUser(
        apiKeyId: ApiKeyId,
        userId: UserId,
    ): ApiUserProfile? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            if (userId !in clients.approvedUserIdsIn(this, client(this, apiKeyId).uid)) return@commitTransaction null
            return@commitTransaction users.apiUserIn(this, userId)
        }

    fun membershipsForApprovedUser(
        apiKeyId: ApiKeyId,
        userId: UserId,
    ): List<ClientApiMembershipProjection>? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val oauthClient = client(this, apiKeyId)
            if (userId !in clients.approvedUserIdsIn(this, oauthClient.uid)) return@commitTransaction null
            users.apiUserIn(this, userId) ?: return@commitTransaction null
            val memberships = organizations.membershipsForUserIn(this, userId)
            val groups =
                organizations
                    .groupsByIdsIn(
                        this,
                        memberships.mapTo(mutableSetOf()) { it.groupId },
                    ).associateBy { it.id }
            val posts =
                organizations
                    .postsByIdsIn(
                        this,
                        memberships.mapTo(mutableSetOf()) { it.postId },
                    ).associateBy { it.id }
            return@commitTransaction memberships.mapNotNull { membership ->
                val group = groups[membership.groupId] ?: return@mapNotNull null
                val post = posts[membership.postId] ?: return@mapNotNull null
                ClientApiMembershipProjection(group, post)
            }
        }

    fun authorities(apiKeyId: ApiKeyId): List<ClientAuthority> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            return@commitTransaction clients.authoritiesIn(this, client(this, apiKeyId).uid)
        }

    fun authoritiesForUser(
        apiKeyId: ApiKeyId,
        userId: UserId,
    ): List<AuthorityName> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val oauthClient = client(this, apiKeyId)
            val effectiveAuthorities = clients.authoritiesForUserIn(this, oauthClient.uid, userId).toMutableSet()
            val membershipSuperGroupIds =
                organizations
                    .superGroupIdsForUserIn(
                        this,
                        userId,
                    ).mapTo(mutableSetOf()) { it.value }
            clients
                .authoritiesIn(this, oauthClient.uid)
                .filter { authority -> authority.superGroupIds.any { it in membershipSuperGroupIds } }
                .forEach { effectiveAuthorities += it.name }
            return@commitTransaction effectiveAuthorities.sortedBy { it.value }
        }

    private fun client(
        transaction: JdbcTransaction,
        apiKeyId: ApiKeyId,
    ): OAuthClient =
        clients.findClientByApiKeyIn(transaction, OAuthApiKeyId(apiKeyId.value))
            ?: throw it.chalmers.gamma.platform.core
                .AccessDenied()
}

data class ClientApiMembershipProjection(
    val group: Group,
    val post: Post,
)
