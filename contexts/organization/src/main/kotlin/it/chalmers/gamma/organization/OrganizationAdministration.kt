package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.core.UnitOfWork
import it.chalmers.gamma.platform.core.UserId

class OrganizationAdministration(
    private val organizations: OrganizationStore,
    private val transactions: UnitOfWork,
) {
    fun createSuperGroupType(
        actor: Actor,
        type: SuperGroupType,
    ) {
        requireAdministrator(actor)
        organizations.createSuperGroupType(type)
    }

    fun deleteSuperGroupType(
        actor: Actor,
        type: SuperGroupType,
    ) {
        requireAdministrator(actor)
        organizations.deleteSuperGroupType(type)
    }

    fun createSuperGroup(
        actor: Actor,
        input: NewSuperGroup,
    ): SuperGroupId {
        requireAdministrator(actor)
        return organizations.createSuperGroup(input)
    }

    fun updateSuperGroup(
        actor: Actor,
        superGroup: SuperGroup,
    ) {
        requireAdministrator(actor)
        organizations.updateSuperGroup(superGroup)
    }

    fun deleteSuperGroup(
        actor: Actor,
        id: SuperGroupId,
    ) {
        requireAdministrator(actor)
        organizations.deleteSuperGroup(id)
    }

    fun createGroup(
        actor: Actor,
        input: NewGroup,
        memberships: List<NewGroupMembership>,
    ): GroupId {
        requireAdministrator(actor)
        return transactions.run {
            val id = organizations.createGroup(input)
            organizations.replaceMemberships(
                id,
                memberships.map {
                    Membership(it.userId, id, it.postId, it.unofficialPostName)
                },
            )
            id
        }
    }

    fun updateGroup(
        actor: Actor,
        group: Group,
        memberships: List<Membership>,
    ) {
        requireAdministrator(actor)
        transactions.run {
            organizations.updateGroup(group)
            organizations.replaceMemberships(group.id, memberships)
        }
    }

    fun deleteGroup(
        actor: Actor,
        id: GroupId,
    ) {
        requireAdministrator(actor)
        organizations.deleteGroup(id)
    }

    fun createPost(
        actor: Actor,
        input: NewPost,
    ): PostId {
        requireAdministrator(actor)
        return organizations.createPost(input)
    }

    fun updatePost(
        actor: Actor,
        post: Post,
    ) {
        requireAdministrator(actor)
        organizations.updatePost(post)
    }

    fun deletePost(
        actor: Actor,
        id: PostId,
    ) {
        requireAdministrator(actor)
        organizations.deletePost(id)
    }

    fun reorderPosts(
        actor: Actor,
        ids: List<PostId>,
    ) {
        requireAdministrator(actor)
        organizations.reorderPosts(ids)
    }

    fun changeMyUnofficialPostName(
        actor: Actor,
        groupId: GroupId,
        postId: PostId,
        name: UnofficialPostName,
    ) {
        val userId = actor.userId()
        val isMember =
            organizations.membershipsForGroup(groupId).any {
                it.userId == userId && it.postId == postId
            }
        if (!isMember) throw AccessDenied()
        organizations.changeUnofficialPostName(userId, groupId, postId, name)
    }

    fun setGroupAvatar(
        actor: Actor,
        id: GroupId,
        uri: String?,
    ) {
        requireGroupEditor(actor, id)
        organizations.setGroupAvatar(id, uri)
    }

    fun setGroupBanner(
        actor: Actor,
        id: GroupId,
        uri: String?,
    ) {
        requireGroupEditor(actor, id)
        organizations.setGroupBanner(id, uri)
    }

    private fun requireAdministrator(actor: Actor) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        if (!user.isAdministrator) throw AccessDenied()
    }

    private fun requireGroupEditor(
        actor: Actor,
        groupId: GroupId,
    ) {
        val userId = actor.userId()
        if ((actor as Actor.User).isAdministrator) return
        if (organizations.membershipsForGroup(groupId).none { it.userId == userId }) throw AccessDenied()
    }

    private fun Actor.userId(): UserId {
        val user = this as? Actor.User ?: throw AccessDenied()
        return UserId(user.userId.value)
    }
}

data class NewGroupMembership(
    val userId: UserId,
    val postId: PostId,
    val unofficialPostName: UnofficialPostName,
)
