package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset

/** Database phases for immutable group images. File ownership and cleanup stay in [GroupImages]. */
internal class GroupImagePointers(
    private val database: DatabaseFactory,
) {
    fun readForEditor(
        actor: Actor,
        groupId: GroupId,
        kind: GroupImageKind,
    ): String? {
        val user = actor as? Actor.User ?: throw AccessDenied()
        return database.commitTransaction(readOnly = true) {
            if (!user.isAdministrator) {
                val isMember =
                    MembershipsTable
                        .selectAll()
                        .where {
                            (MembershipsTable.groupId eq groupId.value) and
                                (MembershipsTable.userId eq user.userId.value)
                        }.count() != 0L
                if (!isMember) throw AccessDenied()
            }
            if (GroupsTable.selectAll().where { GroupsTable.id eq groupId.value }.count() != 1L) {
                throw OrganizationNotFound("Group does not exist")
            }
            val image =
                GroupImagesTable
                    .selectAll()
                    .where { GroupImagesTable.groupId eq groupId.value }
                    .limit(1)
                    .firstOrNull()
            when (kind) {
                GroupImageKind.AVATAR -> image?.get(GroupImagesTable.avatarUri)
                GroupImageKind.BANNER -> image?.get(GroupImagesTable.bannerUri)
            }
        }
    }

    // Compensation needs ownership reads even after access revocation or group deletion.
    fun current(
        groupId: GroupId,
        kind: GroupImageKind,
    ): String? =
        database.commitTransaction(readOnly = true) {
            val image =
                GroupImagesTable
                    .selectAll()
                    .where { GroupImagesTable.groupId eq groupId.value }
                    .limit(1)
                    .firstOrNull()
            when (kind) {
                GroupImageKind.AVATAR -> image?.get(GroupImagesTable.avatarUri)
                GroupImageKind.BANNER -> image?.get(GroupImagesTable.bannerUri)
            }
        }

    fun change(
        actor: Actor,
        change: GroupImageChange,
    ) {
        val user = actor as? Actor.User ?: throw AccessDenied()
        val imageName =
            when (change.kind) {
                GroupImageKind.AVATAR -> "Avatar"
                GroupImageKind.BANNER -> "Banner"
            }
        require(change.replacementUri == null || change.replacementUri.length <= 255) { "$imageName URI is too long" }
        database.commitTransaction {
            val group =
                GroupsTable
                    .selectAll()
                    .where { GroupsTable.id eq change.groupId.value }
                    .forUpdate()
                    .limit(1)
                    .firstOrNull()
                    ?: throw OrganizationNotFound("Group does not exist")
            // Group saves lock this row before replacing memberships. Holding one membership also
            // prevents a concurrent user-deletion cascade from revoking it before this write commits.
            if (!user.isAdministrator) {
                val membership =
                    MembershipsTable
                        .selectAll()
                        .where {
                            (MembershipsTable.groupId eq change.groupId.value) and
                                (MembershipsTable.userId eq user.userId.value)
                        }.forUpdate()
                        .limit(1)
                        .firstOrNull()
                if (membership == null) throw AccessDenied()
            }
            val currentVersion = group[GroupsTable.version] ?: 0
            val image =
                GroupImagesTable
                    .selectAll()
                    .where { GroupImagesTable.groupId eq change.groupId.value }
                    .limit(1)
                    .firstOrNull()
            val currentUri =
                when (change.kind) {
                    GroupImageKind.AVATAR -> image?.get(GroupImagesTable.avatarUri)
                    GroupImageKind.BANNER -> image?.get(GroupImagesTable.bannerUri)
                }
            if (currentUri != change.expectedUri) throw OrganizationConflict("Group image has been changed")
            val now = LocalDateTime.now(ZoneOffset.UTC)
            if (image == null) {
                GroupImagesTable.insert {
                    it[groupId] = change.groupId.value
                    it[avatarUri] = if (change.kind == GroupImageKind.AVATAR) change.replacementUri else null
                    it[bannerUri] = if (change.kind == GroupImageKind.BANNER) change.replacementUri else null
                    it[version] = 0
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            } else {
                GroupImagesTable.update({ GroupImagesTable.groupId eq change.groupId.value }) {
                    when (change.kind) {
                        GroupImageKind.AVATAR -> it[avatarUri] = change.replacementUri
                        GroupImageKind.BANNER -> it[bannerUri] = change.replacementUri
                    }
                    it[version] = currentVersion + 1
                    it[updatedAt] = now
                }
            }
            GroupsTable.update({ GroupsTable.id eq change.groupId.value }) {
                it[version] = currentVersion + 1
                it[updatedAt] = now
            }
        }
    }
}
