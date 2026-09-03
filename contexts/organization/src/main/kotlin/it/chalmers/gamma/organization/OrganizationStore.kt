package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.selectAll

@Suppress("TooManyFunctions") // One explicit store owns every read and mutation for organization data.
class OrganizationStore(
    private val database: DatabaseFactory,
) {
    private val mutations = OrganizationMutations(database)

    fun listSuperGroupTypes(): List<SuperGroupType> =
        database.transaction(readOnly = true) {
            SuperGroupTypesTable
                .selectAll()
                .orderBy(SuperGroupTypesTable.name, SortOrder.ASC)
                .map { SuperGroupType(it[SuperGroupTypesTable.name]) }
        }

    fun findSuperGroup(id: SuperGroupId): SuperGroup? =
        database.transaction(readOnly = true) {
            superGroupsWithDescriptions()
                .selectAll()
                .where { SuperGroupsTable.id eq id.value }
                .limit(1)
                .firstOrNull()
                ?.toSuperGroup()
        }

    fun listSuperGroups(type: SuperGroupType? = null): List<SuperGroup> =
        database.transaction(readOnly = true) {
            superGroupsWithDescriptions()
                .selectAll()
                .apply {
                    if (type != null) {
                        where { SuperGroupsTable.type eq type.value }
                    }
                }.orderBy(SuperGroupsTable.name, SortOrder.ASC)
                .map { it.toSuperGroup() }
        }

    fun findGroup(id: GroupId): Group? =
        database.transaction(readOnly = true) {
            groupsWithOrganization()
                .selectAll()
                .where { GroupsTable.id eq id.value }
                .limit(1)
                .firstOrNull()
                ?.toGroup()
        }

    fun listGroups(superGroupId: SuperGroupId? = null): List<Group> =
        database.transaction(readOnly = true) {
            groupsWithOrganization()
                .selectAll()
                .apply {
                    if (superGroupId != null) {
                        where { GroupsTable.superGroupId eq superGroupId.value }
                    }
                }.orderBy(GroupsTable.name, SortOrder.ASC)
                .map { it.toGroup() }
        }

    fun groupsByIds(ids: Set<GroupId>): List<Group> {
        if (ids.isEmpty()) return emptyList()
        return database.transaction(readOnly = true) {
            groupsWithOrganization()
                .selectAll()
                .where { GroupsTable.id inList ids.map(GroupId::value) }
                .map { it.toGroup() }
        }
    }

    fun findPost(id: PostId): Post? =
        database.transaction(readOnly = true) {
            postsWithNames()
                .selectAll()
                .where { PostsTable.id eq id.value }
                .limit(1)
                .firstOrNull()
                ?.toPost()
        }

    fun listPosts(): List<Post> =
        database.transaction(readOnly = true) {
            postsWithNames()
                .selectAll()
                .orderBy(PostsTable.order, SortOrder.ASC)
                .map { it.toPost() }
        }

    fun postsByIds(ids: Set<PostId>): List<Post> {
        if (ids.isEmpty()) return emptyList()
        return database.transaction(readOnly = true) {
            postsWithNames()
                .selectAll()
                .where { PostsTable.id inList ids.map(PostId::value) }
                .map { it.toPost() }
        }
    }

    fun membershipsForUser(userId: UserId): List<Membership> =
        database.transaction(readOnly = true) {
            MembershipsTable
                .selectAll()
                .where { MembershipsTable.userId eq userId.value }
                .orderBy(MembershipsTable.groupId, SortOrder.ASC)
                .map { it.toMembership() }
        }

    fun membershipsForGroup(groupId: GroupId): List<Membership> =
        database.transaction(readOnly = true) {
            MembershipsTable
                .selectAll()
                .where { MembershipsTable.groupId eq groupId.value }
                .orderBy(MembershipsTable.postId, SortOrder.ASC)
                .map { it.toMembership() }
        }

    fun listMemberships(): List<Membership> =
        database.transaction(readOnly = true) {
            MembershipsTable
                .selectAll()
                .orderBy(MembershipsTable.groupId, SortOrder.ASC)
                .orderBy(MembershipsTable.postId, SortOrder.ASC)
                .map { it.toMembership() }
        }

    fun membershipsForUsers(userIds: Set<UserId>): List<Membership> {
        if (userIds.isEmpty()) return emptyList()
        return database.transaction(readOnly = true) {
            MembershipsTable
                .selectAll()
                .where { MembershipsTable.userId inList userIds.map(UserId::value) }
                .map { it.toMembership() }
        }
    }

    fun membershipsForGroups(groupIds: Set<GroupId>): List<Membership> {
        if (groupIds.isEmpty()) return emptyList()
        return database.transaction(readOnly = true) {
            MembershipsTable
                .selectAll()
                .where { MembershipsTable.groupId inList groupIds.map(GroupId::value) }
                .map { it.toMembership() }
        }
    }

    fun superGroupIdsForUser(userId: UserId): Set<SuperGroupId> =
        database.transaction(readOnly = true) {
            MembershipsTable
                .join(
                    otherTable = GroupsTable,
                    joinType = JoinType.INNER,
                    onColumn = MembershipsTable.groupId,
                    otherColumn = GroupsTable.id,
                ).selectAll()
                .where { MembershipsTable.userId eq userId.value }
                .mapTo(mutableSetOf()) { SuperGroupId(it[GroupsTable.superGroupId]) }
        }

    fun isMemberOfAnySuperGroup(
        userId: UserId,
        superGroupIds: Set<SuperGroupId>,
    ): Boolean {
        if (superGroupIds.isEmpty()) return false
        return database.transaction(readOnly = true) {
            MembershipsTable
                .join(
                    otherTable = GroupsTable,
                    joinType = JoinType.INNER,
                    onColumn = MembershipsTable.groupId,
                    otherColumn = GroupsTable.id,
                ).selectAll()
                .where {
                    (MembershipsTable.userId eq userId.value) and
                        (GroupsTable.superGroupId inList superGroupIds.map(SuperGroupId::value))
                }.limit(1)
                .any()
        }
    }

    fun createSuperGroupType(type: SuperGroupType) = mutations.createSuperGroupType(type)

    fun deleteSuperGroupType(type: SuperGroupType) = mutations.deleteSuperGroupType(type)

    fun createSuperGroup(input: NewSuperGroup): SuperGroupId = mutations.createSuperGroup(input)

    fun updateSuperGroup(superGroup: SuperGroup) = mutations.updateSuperGroup(superGroup)

    fun deleteSuperGroup(id: SuperGroupId) = mutations.deleteSuperGroup(id)

    fun createGroup(input: NewGroup): GroupId = mutations.createGroup(input)

    fun updateGroup(group: Group) = mutations.updateGroup(group)

    fun setGroupAvatar(
        id: GroupId,
        uri: String?,
    ) = mutations.setGroupAvatar(id, uri)

    fun setGroupBanner(
        id: GroupId,
        uri: String?,
    ) = mutations.setGroupBanner(id, uri)

    fun compareAndSetGroupImage(change: GroupImageChange) = mutations.compareAndSetGroupImage(change)

    fun deleteGroup(id: GroupId) = mutations.deleteGroup(id)

    fun createPost(input: NewPost): PostId = mutations.createPost(input)

    fun updatePost(post: Post) = mutations.updatePost(post)

    fun deletePost(id: PostId) = mutations.deletePost(id)

    fun reorderPosts(ids: List<PostId>) = mutations.reorderPosts(ids)

    fun replaceMemberships(
        groupId: GroupId,
        memberships: List<Membership>,
    ) = mutations.replaceMemberships(groupId, memberships)

    fun changeUnofficialPostName(
        userId: UserId,
        groupId: GroupId,
        postId: PostId,
        name: UnofficialPostName,
    ) = mutations.changeUnofficialPostName(userId, groupId, postId, name)

    private fun superGroupsWithDescriptions() =
        SuperGroupsTable.join(
            otherTable = LocalizedTextsTable,
            joinType = JoinType.LEFT,
            onColumn = SuperGroupsTable.descriptionId,
            otherColumn = LocalizedTextsTable.id,
        )

    private fun groupsWithOrganization() =
        GroupsTable
            .join(
                otherTable = SuperGroupsTable,
                joinType = JoinType.INNER,
                onColumn = GroupsTable.superGroupId,
                otherColumn = SuperGroupsTable.id,
            ).join(
                otherTable = LocalizedTextsTable,
                joinType = JoinType.LEFT,
                onColumn = SuperGroupsTable.descriptionId,
                otherColumn = LocalizedTextsTable.id,
            ).join(
                otherTable = GroupImagesTable,
                joinType = JoinType.LEFT,
                onColumn = GroupsTable.id,
                otherColumn = GroupImagesTable.groupId,
            )

    private fun postsWithNames() =
        PostsTable.join(
            otherTable = LocalizedTextsTable,
            joinType = JoinType.INNER,
            onColumn = PostsTable.nameId,
            otherColumn = LocalizedTextsTable.id,
        )

    private fun ResultRow.toLocalizedText(): LocalizedText =
        LocalizedText.of(
            sv = getOrNull(LocalizedTextsTable.sv).orEmpty(),
            en = getOrNull(LocalizedTextsTable.en).orEmpty(),
        )

    private fun ResultRow.toSuperGroup(): SuperGroup =
        SuperGroup(
            id = SuperGroupId(this[SuperGroupsTable.id]),
            version = this[SuperGroupsTable.version] ?: 0,
            name = OrganizationName(this[SuperGroupsTable.name]),
            prettyName = PrettyName(this[SuperGroupsTable.prettyName]),
            type = SuperGroupType(this[SuperGroupsTable.type]),
            description = toLocalizedText(),
        )

    private fun ResultRow.toGroup(): Group =
        Group(
            id = GroupId(this[GroupsTable.id]),
            version = this[GroupsTable.version] ?: 0,
            name = OrganizationName(this[GroupsTable.name]),
            prettyName = PrettyName(this[GroupsTable.prettyName]),
            superGroup = toSuperGroup(),
            avatarUri = this[GroupImagesTable.avatarUri],
            bannerUri = this[GroupImagesTable.bannerUri],
        )

    private fun ResultRow.toPost(): Post =
        Post(
            id = PostId(this[PostsTable.id]),
            version = this[PostsTable.version] ?: 0,
            name = toLocalizedText(),
            emailPrefix = EmailPrefix(this[PostsTable.emailPrefix] ?: ""),
            order = PostOrder(this[PostsTable.order] ?: 0),
        )

    private fun ResultRow.toMembership(): Membership =
        Membership(
            userId = UserId(this[MembershipsTable.userId]),
            groupId = GroupId(this[MembershipsTable.groupId]),
            postId = PostId(this[MembershipsTable.postId]),
            unofficialPostName = UnofficialPostName(this[MembershipsTable.unofficialPostName]),
        )
}
