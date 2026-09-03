package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

internal class OrganizationMutations(
    private val database: DatabaseFactory,
) {
    fun createSuperGroupType(type: SuperGroupType) {
        database.transaction {
            if (SuperGroupTypesTable.selectAll().where { SuperGroupTypesTable.name eq type.value }.count() != 0L) {
                throw OrganizationConflict("Super group type already exists")
            }
            SuperGroupTypesTable.insert {
                it[name] = type.value
                it[createdAt] = now()
            }
        }
    }

    fun deleteSuperGroupType(type: SuperGroupType) {
        database.transaction {
            if (SuperGroupsTable.selectAll().where { SuperGroupsTable.type eq type.value }.count() != 0L) {
                throw OrganizationConflict("Super group type is still in use")
            }
            if (SuperGroupTypesTable.deleteWhere { SuperGroupTypesTable.name eq type.value } != 1) {
                throw OrganizationNotFound("Super group type does not exist")
            }
        }
    }

    fun createSuperGroup(input: NewSuperGroup): SuperGroupId =
        database.transaction {
            val id = SuperGroupId.generate()
            val textId = insertText(input.description)
            val now = now()
            SuperGroupsTable.insert {
                it[SuperGroupsTable.id] = id.value
                it[name] = input.name.value
                it[prettyName] = input.prettyName.value
                it[type] = input.type.value
                it[descriptionId] = textId
                it[version] = 0
                it[createdAt] = now
                it[updatedAt] = now
            }
            id
        }

    fun updateSuperGroup(superGroup: SuperGroup) {
        database.transaction {
            val row =
                SuperGroupsTable
                    .selectAll()
                    .where {
                        (SuperGroupsTable.id eq superGroup.id.value) and
                            SuperGroupsTable.version.matchesStoredVersion(superGroup.version)
                    }.limit(1)
                    .firstOrNull()
                    ?: throw OrganizationConflict("Super group is missing or has been changed")
            val descriptionId = row[SuperGroupsTable.descriptionId]

            val changed =
                SuperGroupsTable.update(
                    where = {
                        (SuperGroupsTable.id eq superGroup.id.value) and
                            SuperGroupsTable.version.matchesStoredVersion(superGroup.version)
                    },
                ) {
                    it[name] = superGroup.name.value
                    it[prettyName] = superGroup.prettyName.value
                    it[type] = superGroup.type.value
                    it[version] = superGroup.version + 1
                    it[updatedAt] = now()
                }
            if (changed != 1) throw OrganizationConflict("Super group is missing or has been changed")
            if (descriptionId == null) {
                val newTextId = insertText(superGroup.description)
                SuperGroupsTable.update({ SuperGroupsTable.id eq superGroup.id.value }) {
                    it[SuperGroupsTable.descriptionId] = newTextId
                }
            } else {
                updateText(descriptionId, superGroup.description)
            }
        }
    }

    fun deleteSuperGroup(id: SuperGroupId) {
        database.transaction {
            val row =
                SuperGroupsTable
                    .selectAll()
                    .where { SuperGroupsTable.id eq id.value }
                    .limit(1)
                    .firstOrNull()
                    ?: throw OrganizationNotFound("Super group does not exist")
            val descriptionId = row[SuperGroupsTable.descriptionId]
            if (GroupsTable.selectAll().where { GroupsTable.superGroupId eq id.value }.count() != 0L) {
                throw OrganizationConflict("Super group is still used by groups")
            }
            SuperGroupsTable.deleteWhere { SuperGroupsTable.id eq id.value }
            if (descriptionId != null) LocalizedTextsTable.deleteWhere { LocalizedTextsTable.id eq descriptionId }
        }
    }

    fun createGroup(input: NewGroup): GroupId =
        database.transaction {
            if (SuperGroupsTable.selectAll().where { SuperGroupsTable.id eq input.superGroupId.value }.count() != 1L) {
                throw OrganizationNotFound("Super group does not exist")
            }
            val id = GroupId.generate()
            val now = now()
            GroupsTable.insert {
                it[GroupsTable.id] = id.value
                it[name] = input.name.value
                it[prettyName] = input.prettyName.value
                it[superGroupId] = input.superGroupId.value
                it[version] = 0
                it[createdAt] = now
                it[updatedAt] = now
            }
            id
        }

    fun updateGroup(group: Group) =
        database.transaction {
            val changed =
                GroupsTable.update(
                    where = {
                        (GroupsTable.id eq group.id.value) and
                            GroupsTable.version.matchesStoredVersion(group.version)
                    },
                ) {
                    it[name] = group.name.value
                    it[prettyName] = group.prettyName.value
                    it[superGroupId] = group.superGroup.id.value
                    it[version] = group.version + 1
                    it[updatedAt] = now()
                }
            if (changed != 1) throw OrganizationConflict("Group is missing or has been changed")
        }

    fun setGroupAvatar(
        id: GroupId,
        uri: String?,
    ) = setGroupImage(id, uri, GroupImageKind.AVATAR, GroupImageWriteCondition.Unconditional)

    fun setGroupBanner(
        id: GroupId,
        uri: String?,
    ) = setGroupImage(id, uri, GroupImageKind.BANNER, GroupImageWriteCondition.Unconditional)

    fun compareAndSetGroupImage(change: GroupImageChange) =
        setGroupImage(
            change.groupId,
            change.replacementUri,
            change.kind,
            GroupImageWriteCondition.CurrentUri(change.expectedUri),
        )

    private val GroupImageKind.displayName: String
        get() =
            when (this) {
                GroupImageKind.AVATAR -> "Avatar"
                GroupImageKind.BANNER -> "Banner"
            }

    private fun setGroupImage(
        id: GroupId,
        uri: String?,
        kind: GroupImageKind,
        condition: GroupImageWriteCondition,
    ) = database.transaction {
        require(uri == null || uri.length <= 255) { "${kind.displayName} URI is too long" }
        val group =
            GroupsTable
                .selectAll()
                .where { GroupsTable.id eq id.value }
                .forUpdate()
                .limit(1)
                .firstOrNull()
                ?: throw OrganizationNotFound("Group does not exist")
        val currentVersion = group[GroupsTable.version] ?: 0
        val imageRow =
            GroupImagesTable
                .selectAll()
                .where { GroupImagesTable.groupId eq id.value }
                .limit(1)
                .firstOrNull()
        val currentUri =
            imageRow?.let {
                when (kind) {
                    GroupImageKind.AVATAR -> it[GroupImagesTable.avatarUri]
                    GroupImageKind.BANNER -> it[GroupImagesTable.bannerUri]
                }
            }
        if (condition is GroupImageWriteCondition.CurrentUri && currentUri != condition.uri) {
            throw OrganizationConflict("Group image has been changed")
        }
        if (imageRow == null) {
            GroupImagesTable.insert {
                it[groupId] = id.value
                it[avatarUri] = uri.takeIf { kind == GroupImageKind.AVATAR }
                it[bannerUri] = uri.takeIf { kind == GroupImageKind.BANNER }
                it[version] = 0
                it[createdAt] = now()
                it[updatedAt] = now()
            }
        } else {
            GroupImagesTable.update({ GroupImagesTable.groupId eq id.value }) {
                when (kind) {
                    GroupImageKind.AVATAR -> it[avatarUri] = uri
                    GroupImageKind.BANNER -> it[bannerUri] = uri
                }
                it[version] = currentVersion + 1
                it[updatedAt] = now()
            }
        }
        GroupsTable.update({ GroupsTable.id eq id.value }) {
            it[version] = currentVersion + 1
            it[updatedAt] = now()
        }
        Unit
    }

    private sealed interface GroupImageWriteCondition {
        data object Unconditional : GroupImageWriteCondition

        data class CurrentUri(
            val uri: String?,
        ) : GroupImageWriteCondition
    }

    fun deleteGroup(id: GroupId) =
        database.transaction {
            if (GroupsTable.deleteWhere { GroupsTable.id eq id.value } != 1) {
                throw OrganizationNotFound("Group does not exist")
            }
        }

    fun createPost(input: NewPost): PostId =
        database.transaction {
            require(
                input.name.sv.value
                    .isNotEmpty() &&
                    input.name.en.value
                        .isNotEmpty(),
            ) {
                "Post names must not be empty"
            }
            val id = PostId.generate()
            val textId = insertText(input.name)
            val now = now()
            val order = PostsTable.selectAll().count().toInt()
            PostsTable.insert {
                it[PostsTable.id] = id.value
                it[nameId] = textId
                it[emailPrefix] = input.emailPrefix.value
                it[version] = 0
                it[PostsTable.order] = order
                it[createdAt] = now
                it[updatedAt] = now
            }
            id
        }

    fun updatePost(post: Post) =
        database.transaction {
            val nameId =
                PostsTable
                    .selectAll()
                    .where {
                        (PostsTable.id eq post.id.value) and
                            PostsTable.version.matchesStoredVersion(post.version)
                    }.limit(1)
                    .firstOrNull()
                    ?.get(PostsTable.nameId)
                    ?: throw OrganizationConflict("Post is missing or has been changed")
            val changed =
                PostsTable.update(
                    where = {
                        (PostsTable.id eq post.id.value) and
                            PostsTable.version.matchesStoredVersion(post.version)
                    },
                ) {
                    it[emailPrefix] = post.emailPrefix.value
                    it[version] = post.version + 1
                    it[order] = post.order.value
                    it[updatedAt] = now()
                }
            if (changed != 1) throw OrganizationConflict("Post is missing or has been changed")
            updateText(nameId, post.name)
        }

    fun deletePost(id: PostId) {
        database.transaction {
            val nameId =
                PostsTable
                    .selectAll()
                    .where { PostsTable.id eq id.value }
                    .limit(1)
                    .firstOrNull()
                    ?.get(PostsTable.nameId)
                    ?: throw OrganizationNotFound("Post does not exist")
            if (MembershipsTable.selectAll().where { MembershipsTable.postId eq id.value }.count() != 0L) {
                throw OrganizationConflict("Post is still used by memberships")
            }
            PostsTable.deleteWhere { PostsTable.id eq id.value }
            LocalizedTextsTable.deleteWhere { LocalizedTextsTable.id eq nameId }
        }
    }

    fun reorderPosts(ids: List<PostId>) =
        database.transaction {
            val existing = PostsTable.selectAll().map { PostId(it[PostsTable.id]) }.toSet()
            require(ids.size == existing.size && ids.toSet() == existing) {
                "The order must contain every post exactly once"
            }
            ids.forEachIndexed { index, id ->
                PostsTable.update({ PostsTable.id eq id.value }) {
                    it[order] = index
                    it[updatedAt] = now()
                }
            }
        }

    fun replaceMemberships(
        groupId: GroupId,
        memberships: List<Membership>,
    ) = database.transaction {
        require(memberships.all { it.groupId == groupId }) { "Every membership must belong to the group" }
        if (GroupsTable.selectAll().where { GroupsTable.id eq groupId.value }.count() != 1L) {
            throw OrganizationNotFound("Group does not exist")
        }
        MembershipsTable.deleteWhere { MembershipsTable.groupId eq groupId.value }
        memberships.forEach { membership ->
            MembershipsTable.insert {
                it[createdAt] = now()
                it[userId] = membership.userId.value
                it[MembershipsTable.groupId] = membership.groupId.value
                it[postId] = membership.postId.value
                it[unofficialPostName] = membership.unofficialPostName.value
            }
        }
    }

    fun changeUnofficialPostName(
        userId: UserId,
        groupId: GroupId,
        postId: PostId,
        name: UnofficialPostName,
    ) = database.transaction {
        val changed =
            MembershipsTable.update(
                where = {
                    (MembershipsTable.userId eq userId.value) and
                        (MembershipsTable.groupId eq groupId.value) and
                        (MembershipsTable.postId eq postId.value)
                },
            ) {
                it[unofficialPostName] = name.value
            }
        if (changed != 1) throw OrganizationNotFound("Membership does not exist")
    }

    private fun org.jetbrains.exposed.v1.jdbc.JdbcTransaction.insertText(text: LocalizedText): UUID {
        val id = UUID.randomUUID()
        LocalizedTextsTable.insert {
            it[LocalizedTextsTable.id] = id
            it[sv] = text.sv.value
            it[en] = text.en.value
            it[createdAt] = now()
        }
        return id
    }

    private fun org.jetbrains.exposed.v1.jdbc.JdbcTransaction.updateText(
        id: UUID,
        text: LocalizedText,
    ) {
        LocalizedTextsTable.update({ LocalizedTextsTable.id eq id }) {
            it[sv] = text.sv.value
            it[en] = text.en.value
        }
    }

    private fun now(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
}
