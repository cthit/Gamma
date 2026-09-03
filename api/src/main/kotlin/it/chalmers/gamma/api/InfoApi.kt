package it.chalmers.gamma.api

import it.chalmers.gamma.apiaccess.ApiAccessNotFound
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyStore
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.OrganizationStore
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.organization.SuperGroupType
import it.chalmers.gamma.organization.UnofficialPostName
import it.chalmers.gamma.users.ApiUserProfile
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserStore

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
    private val apiKeys: ApiKeyStore,
    private val users: UserStore,
    private val organizations: OrganizationStore,
) {
    fun user(
        apiKeyId: ApiKeyId,
        userId: UserId,
    ): InfoUserProjection? {
        apiKeys.infoSettings(apiKeyId) ?: throw ApiAccessNotFound("API key settings do not exist")
        val user = users.apiUser(userId) ?: return null
        val memberships = organizations.membershipsForUser(userId)
        val groups = organizations.groupsByIds(memberships.mapTo(mutableSetOf()) { it.groupId }).associateBy { it.id }
        val posts = organizations.postsByIds(memberships.mapTo(mutableSetOf()) { it.postId }).associateBy { it.id }
        return InfoUserProjection(
            user,
            memberships.mapNotNull { membership ->
                val group = groups[membership.groupId] ?: return@mapNotNull null
                val post = posts[membership.postId] ?: return@mapNotNull null
                InfoMembershipProjection(group, post)
            },
        )
    }

    fun blob(apiKeyId: ApiKeyId): List<InfoBlobProjection> {
        val enabledTypes =
            apiKeys.infoSettings(apiKeyId)?.superGroupTypes
                ?: throw ApiAccessNotFound("API key settings do not exist")
        if (enabledTypes.isEmpty()) return emptyList()

        val superGroups = organizations.listSuperGroups()
        val groupsBySuperGroup = organizations.listGroups().groupBy { it.superGroup.id }
        val membershipsByGroup = organizations.listMemberships().groupBy { it.groupId }
        val users = users.apiUsers().associateBy { it.id }
        val posts = organizations.listPosts().associateBy { it.id }
        return enabledTypes.map { type ->
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
