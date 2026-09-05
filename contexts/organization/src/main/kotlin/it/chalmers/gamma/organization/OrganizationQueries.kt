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
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.sql.Connection

class OrganizationQueries(
    private val database: DatabaseFactory,
) {
    fun listSuperGroupTypes(): List<SuperGroupType> =
        database.commitTransaction(readOnly = true) {
            SuperGroupTypesTable
                .selectAll()
                .orderBy(SuperGroupTypesTable.name, SortOrder.ASC)
                .map { SuperGroupType(it[SuperGroupTypesTable.name]) }
        }

    fun superGroupDetails(id: SuperGroupId): SuperGroupDetails? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val superGroup =
                superGroupsWithDescriptions()
                    .selectAll()
                    .where { SuperGroupsTable.id eq id.value }
                    .limit(1)
                    .firstOrNull()
                    ?.toSuperGroup() ?: return@commitTransaction null
            SuperGroupDetails(superGroup, listGroupsIn(this, id))
        }

    fun superGroupEditor(id: SuperGroupId): SuperGroupEditor? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val superGroup =
                superGroupsWithDescriptions()
                    .selectAll()
                    .where { SuperGroupsTable.id eq id.value }
                    .limit(1)
                    .firstOrNull()
                    ?.toSuperGroup() ?: return@commitTransaction null
            val types =
                SuperGroupTypesTable
                    .selectAll()
                    .orderBy(SuperGroupTypesTable.name, SortOrder.ASC)
                    .map { SuperGroupType(it[SuperGroupTypesTable.name]) }
            SuperGroupEditor(superGroup, types)
        }

    fun listSuperGroups(type: SuperGroupType? = null): List<SuperGroup> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            listSuperGroupsIn(this, type)
        }

    fun listSuperGroupsIn(
        transaction: JdbcTransaction,
        type: SuperGroupType? = null,
    ): List<SuperGroup> {
        database.requireTransaction(transaction)
        return superGroupsWithDescriptions()
            .selectAll()
            .apply {
                if (type != null) {
                    where { SuperGroupsTable.type eq type.value }
                }
            }.orderBy(SuperGroupsTable.name, SortOrder.ASC)
            .map { it.toSuperGroup() }
    }

    fun findGroup(id: GroupId): Group? =
        database.commitTransaction(readOnly = true) {
            findGroupIn(this, id)
        }

    fun findGroupIn(
        transaction: JdbcTransaction,
        id: GroupId,
    ): Group? {
        database.requireTransaction(transaction)
        return groupsWithOrganization()
            .selectAll()
            .where { GroupsTable.id eq id.value }
            .limit(1)
            .firstOrNull()
            ?.toGroup()
    }

    fun listGroups(superGroupId: SuperGroupId? = null): List<Group> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            listGroupsIn(this, superGroupId)
        }

    fun listGroupsIn(
        transaction: JdbcTransaction,
        superGroupId: SuperGroupId? = null,
    ): List<Group> {
        database.requireTransaction(transaction)
        return groupsWithOrganization()
            .selectAll()
            .apply {
                if (superGroupId != null) {
                    where { GroupsTable.superGroupId eq superGroupId.value }
                }
            }.orderBy(GroupsTable.name, SortOrder.ASC)
            .map { it.toGroup() }
    }

    fun groupsByIdsIn(
        transaction: JdbcTransaction,
        ids: Set<GroupId>,
    ): List<Group> {
        database.requireTransaction(transaction)
        if (ids.isEmpty()) return emptyList()
        return groupsWithOrganization()
            .selectAll()
            .where { GroupsTable.id inList ids.map(GroupId::value) }
            .map { it.toGroup() }
    }

    fun findPost(id: PostId): Post? =
        database.commitTransaction(readOnly = true) {
            postsWithNames()
                .selectAll()
                .where { PostsTable.id eq id.value }
                .limit(1)
                .firstOrNull()
                ?.toPost()
        }

    fun listPosts(): List<Post> =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            listPostsIn(this)
        }

    fun listPostsIn(transaction: JdbcTransaction): List<Post> {
        database.requireTransaction(transaction)
        return postsWithNames()
            .selectAll()
            .orderBy(PostsTable.order, SortOrder.ASC)
            .map { it.toPost() }
    }

    fun postsByIdsIn(
        transaction: JdbcTransaction,
        ids: Set<PostId>,
    ): List<Post> {
        database.requireTransaction(transaction)
        if (ids.isEmpty()) return emptyList()
        return postsWithNames()
            .selectAll()
            .where { PostsTable.id inList ids.map(PostId::value) }
            .map { it.toPost() }
    }

    fun membershipsForUserIn(
        transaction: JdbcTransaction,
        userId: UserId,
    ): List<Membership> {
        database.requireTransaction(transaction)
        return MembershipsTable
            .selectAll()
            .where { MembershipsTable.userId eq userId.value }
            .orderBy(MembershipsTable.groupId, SortOrder.ASC)
            .map { it.toMembership() }
    }

    fun membershipsForGroupIn(
        transaction: JdbcTransaction,
        groupId: GroupId,
    ): List<Membership> {
        database.requireTransaction(transaction)
        return MembershipsTable
            .selectAll()
            .where { MembershipsTable.groupId eq groupId.value }
            .orderBy(MembershipsTable.postId, SortOrder.ASC)
            .map { it.toMembership() }
    }

    fun listMembershipsIn(transaction: JdbcTransaction): List<Membership> {
        database.requireTransaction(transaction)
        return MembershipsTable
            .selectAll()
            .orderBy(MembershipsTable.groupId, SortOrder.ASC)
            .orderBy(MembershipsTable.postId, SortOrder.ASC)
            .map { it.toMembership() }
    }

    fun superGroupIdsForUserIn(
        transaction: JdbcTransaction,
        userId: UserId,
    ): Set<SuperGroupId> {
        database.requireTransaction(transaction)
        return MembershipsTable
            .join(
                otherTable = GroupsTable,
                joinType = JoinType.INNER,
                onColumn = MembershipsTable.groupId,
                otherColumn = GroupsTable.id,
            ).selectAll()
            .where { MembershipsTable.userId eq userId.value }
            .mapTo(mutableSetOf()) { SuperGroupId(it[GroupsTable.superGroupId]) }
    }

    fun isMemberOfAnySuperGroupIn(
        transaction: JdbcTransaction,
        userId: UserId,
        superGroupIds: Set<SuperGroupId>,
    ): Boolean {
        database.requireTransaction(transaction)
        if (superGroupIds.isEmpty()) return false
        return MembershipsTable
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
