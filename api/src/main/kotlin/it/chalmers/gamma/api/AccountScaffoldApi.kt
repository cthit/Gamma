package it.chalmers.gamma.api

import it.chalmers.gamma.apiaccess.ApiAccessNotFound
import it.chalmers.gamma.apiaccess.ApiKeyId
import it.chalmers.gamma.apiaccess.ApiKeyQueries
import it.chalmers.gamma.organization.Group
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.organization.Post
import it.chalmers.gamma.organization.SuperGroup
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.ApiUserProfile
import it.chalmers.gamma.users.UserQueries
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import java.sql.Connection

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
    private val database: DatabaseFactory,
    private val apiKeys: ApiKeyQueries,
    private val users: UserQueries,
    private val organizations: OrganizationQueries,
) {
    fun superGroups(apiKeyId: ApiKeyId): List<AccountScaffoldSuperGroupProjection> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val settings = settings(this, apiKeyId).superGroupTypes.associateBy { it.type }
            if (settings.isEmpty()) return@commitTransaction emptyList()

            val users = users.apiUsersIn(this).associateBy { it.id }
            val posts = organizations.listPostsIn(this).associateBy { it.id }
            val groupsBySuperGroup =
                organizations
                    .listGroupsIn(this)
                    .filter { it.superGroup.type in settings }
                    .groupBy { it.superGroup.id }
            val membershipsByGroup = organizations.listMembershipsIn(this).groupBy { it.groupId }
            return@commitTransaction organizations
                .listSuperGroupsIn(this)
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

    fun users(apiKeyId: ApiKeyId): List<ApiUserProfile> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val managedTypes =
                settings(this, apiKeyId)
                    .superGroupTypes
                    .filter { it.requiresManaged }
                    .mapTo(mutableSetOf()) { it.type }
            if (managedTypes.isEmpty()) return@commitTransaction emptyList()

            val managedGroupIds =
                organizations
                    .listGroupsIn(this)
                    .filter { it.superGroup.type in managedTypes }
                    .mapTo(mutableSetOf()) { it.id }
            val membershipsByUser = organizations.listMembershipsIn(this).groupBy { it.userId }
            return@commitTransaction users
                .apiUsersIn(this)
                .filter { user ->
                    user.gdprTrained && membershipsByUser[user.id].orEmpty().any { it.groupId in managedGroupIds }
                }.sortedBy { it.cid.value }
        }

    private fun settings(
        transaction: JdbcTransaction,
        apiKeyId: ApiKeyId,
    ) = apiKeys.accountScaffoldSettingsIn(transaction, apiKeyId)
        ?: throw ApiAccessNotFound("API key settings do not exist")
}
