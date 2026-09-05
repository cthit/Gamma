package it.chalmers.gamma.api

import it.chalmers.gamma.apiaccess.ApiAccessNotFound
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.organization.SuperGroupType
import it.chalmers.gamma.organization.UnofficialPostName
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.ApiUserProfile
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import java.sql.Connection

data class InfoUserProjection(
    val user: ApiUserProfile,
    val memberships: List<InfoMembershipProjection>,
)

data class InfoMembershipProjection(
    val group: Group,
    val post: Post,
)

data class InfoBlobProjection(
    val type: SuperGroupType,
    val superGroups: List<InfoBlobSuperGroupProjection>,
)

data class InfoBlobSuperGroupProjection(
    val superGroup: SuperGroup,
    val hasBanner: Boolean,
    val hasAvatar: Boolean,
    val members: List<InfoBlobMemberProjection>,
)

data class InfoBlobMemberProjection(
    val user: ApiUserProfile,
    val post: Post,
    val unofficialPostName: UnofficialPostName,
)

class InfoApi(
    private val database: DatabaseFactory,
    private val apiKeys: ApiKeyQueries,
    private val users: UserQueries,
    private val organizations: OrganizationQueries,
) {
    fun user(
        apiKeyId: ApiKeyId,
        userId: UserId,
    ): InfoUserProjection? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            apiKeys.infoSettingsIn(this, apiKeyId) ?: throw ApiAccessNotFound("API key settings do not exist")
            val user = users.apiUserIn(this, userId) ?: return@commitTransaction null
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
            return@commitTransaction InfoUserProjection(
                user,
                memberships.mapNotNull { membership ->
                    val group = groups[membership.groupId] ?: return@mapNotNull null
                    val post = posts[membership.postId] ?: return@mapNotNull null
                    InfoMembershipProjection(group, post)
                },
            )
        }

    fun blob(apiKeyId: ApiKeyId): List<InfoBlobProjection> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val enabledTypes =
                apiKeys.infoSettingsIn(this, apiKeyId)?.superGroupTypes
                    ?: throw ApiAccessNotFound("API key settings do not exist")
            if (enabledTypes.isEmpty()) return@commitTransaction emptyList()

            val superGroups = organizations.listSuperGroupsIn(this)
            val groupsBySuperGroup = organizations.listGroupsIn(this).groupBy { it.superGroup.id }
            val membershipsByGroup = organizations.listMembershipsIn(this).groupBy { it.groupId }
            val users = users.apiUsersIn(this).associateBy { it.id }
            val posts = organizations.listPostsIn(this).associateBy { it.id }
            return@commitTransaction enabledTypes.map { type ->
                InfoBlobProjection(
                    type,
                    superGroups.filter { it.type == type }.map { superGroup ->
                        val childGroups = groupsBySuperGroup[superGroup.id].orEmpty()
                        val members =
                            childGroups
                                .flatMap { group -> membershipsByGroup[group.id].orEmpty() }
                                .mapNotNull { membership ->
                                    val user = users[membership.userId] ?: return@mapNotNull null
                                    val post = posts[membership.postId] ?: return@mapNotNull null
                                    InfoBlobMemberProjection(user, post, membership.unofficialPostName)
                                }.sortedBy { it.post.order.value }
                        InfoBlobSuperGroupProjection(
                            superGroup,
                            hasBanner = childGroups.any { it.bannerUri != null },
                            hasAvatar = childGroups.any { it.avatarUri != null },
                            members,
                        )
                    },
                )
            }
        }
}
