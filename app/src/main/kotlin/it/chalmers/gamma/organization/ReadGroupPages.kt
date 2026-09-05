package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.DirectoryUser
import it.chalmers.gamma.users.UserAccountAccess
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserQueries
import java.sql.Connection

class ReadGroupPages(
    private val database: DatabaseFactory,
    private val accounts: UserAccountAccess,
    private val organizations: OrganizationQueries,
    private val users: UserQueries,
) {
    fun details(
        actor: Actor,
        groupId: GroupId,
    ): GroupDetailsPage? =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            if (!account.isAdministrator) throw AccessDenied()
            val group = organizations.findGroupIn(this, groupId) ?: return@commitTransaction null
            val memberships = organizations.membershipsForGroupIn(this, groupId)
            val members = users.directoryUsersByIdsIn(this, memberships.map { it.userId }.toSet())
            val posts = organizations.postsByIdsIn(this, memberships.map { it.postId }.toSet())
            GroupDetailsPage(
                group,
                memberships,
                members.associateBy { it.id },
                posts.associateBy { it.id },
                account.userId,
            )
        }

    fun editor(
        actor: Actor,
        groupId: GroupId,
    ): GroupEditor? =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            if (!account.isAdministrator) throw AccessDenied()
            val group = organizations.findGroupIn(this, groupId) ?: return@commitTransaction null
            val memberships = organizations.membershipsForGroupIn(this, groupId)
            GroupEditor(
                superGroups = organizations.listSuperGroupsIn(this),
                group = group,
                users = users.administratorDirectoryUsersIn(this, account.userId),
                posts = organizations.listPostsIn(this),
                memberships = memberships,
            )
        }

    fun newMember(actor: Actor): GroupMemberOptions =
        database.commitTransaction(readOnly = false, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val account = accounts.requireIn(this, actor)
            if (!account.isAdministrator) throw AccessDenied()
            GroupMemberOptions(
                users.administratorDirectoryUsersIn(this, account.userId),
                organizations.listPostsIn(this),
            )
        }
}

data class GroupEditor(
    val superGroups: List<SuperGroup>,
    val group: Group? = null,
    val users: List<DirectoryUser> = emptyList(),
    val posts: List<Post> = emptyList(),
    val memberships: List<Membership> = emptyList(),
) {
    override fun toString(): String = "GroupEditor(<redacted>)"
}

data class GroupDetailsPage(
    val group: Group,
    val memberships: List<Membership>,
    val users: Map<UserId, DirectoryUser>,
    val posts: Map<PostId, Post>,
    val ownUserId: UserId,
) {
    override fun toString(): String = "GroupDetailsPage(<redacted>)"
}

data class GroupMemberOptions(
    val users: List<DirectoryUser>,
    val posts: List<Post>,
) {
    override fun toString(): String = "GroupMemberOptions(<redacted>)"
}
