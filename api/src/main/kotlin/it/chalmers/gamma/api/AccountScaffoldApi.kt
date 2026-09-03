package it.chalmers.gamma.api

import it.chalmers.gamma.apiaccess.ApiAccessNotFound
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyStore
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.OrganizationStore
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.users.ApiUserProfile
import it.chalmers.gamma.users.UserStore

data class AccountScaffoldSuperGroupProjection(
    val superGroup: SuperGroup,
    val groups: List<AccountScaffoldGroupProjection>,
    val useManagedAccount: Boolean,
)

data class AccountScaffoldGroupProjection(
    val group: Group,
    val members: List<AccountScaffoldMemberProjection>,
)

data class AccountScaffoldMemberProjection(
    val post: Post,
    val user: ApiUserProfile,
)

class AccountScaffoldApi(
    private val apiKeys: ApiKeyStore,
    private val users: UserStore,
    private val organizations: OrganizationStore,
) {
    fun superGroups(apiKeyId: ApiKeyId): List<AccountScaffoldSuperGroupProjection> {
        val settings = settings(apiKeyId).superGroupTypes.associateBy { it.type }
        if (settings.isEmpty()) return emptyList()

        val users = users.apiUsers().associateBy { it.id }
        val posts = organizations.listPosts().associateBy { it.id }
        val groupsBySuperGroup =
            organizations
                .listGroups()
                .filter { it.superGroup.type in settings }
                .groupBy { it.superGroup.id }
        val membershipsByGroup = organizations.listMemberships().groupBy { it.groupId }
        return organizations
            .listSuperGroups()
            .filter { it.type in settings && it.id in groupsBySuperGroup }
            .map { superGroup ->
                val managed = settings.getValue(superGroup.type).requiresManaged
                AccountScaffoldSuperGroupProjection(
                    superGroup,
                    groupsBySuperGroup[superGroup.id].orEmpty().map { group ->
                        val members =
                            membershipsByGroup[group.id].orEmpty().mapNotNull { membership ->
                                val user = users[membership.userId] ?: return@mapNotNull null
                                if (managed && !user.gdprTrained) return@mapNotNull null
                                val post = posts[membership.postId] ?: return@mapNotNull null
                                AccountScaffoldMemberProjection(post, user)
                            }
                        AccountScaffoldGroupProjection(group, members)
                    },
                    managed,
                )
            }
    }

    fun users(apiKeyId: ApiKeyId): List<ApiUserProfile> {
        val managedTypes =
            settings(apiKeyId)
                .superGroupTypes
                .filter { it.requiresManaged }
                .mapTo(mutableSetOf()) { it.type }
        if (managedTypes.isEmpty()) return emptyList()

        val managedGroupIds =
            organizations
                .listGroups()
                .filter { it.superGroup.type in managedTypes }
                .mapTo(mutableSetOf()) { it.id }
        val membershipsByUser = organizations.listMemberships().groupBy { it.userId }
        return users
            .apiUsers()
            .filter { user ->
                user.gdprTrained && membershipsByUser[user.id].orEmpty().any { it.groupId in managedGroupIds }
            }.sortedBy { it.cid.value }
    }

    private fun settings(apiKeyId: ApiKeyId) =
        apiKeys.accountScaffoldSettings(apiKeyId)
            ?: throw ApiAccessNotFound("API key settings do not exist")
}
