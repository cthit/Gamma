package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset

data class GroupUpdate(
    val groupId: GroupId,
    val expectedVersion: Int,
    val name: OrganizationName,
    val prettyName: PrettyName,
    val superGroupId: SuperGroupId,
    val memberships: List<NewGroupMembership>,
)

class UpdateGroup(
    private val database: DatabaseFactory,
) {
    // The version claim and membership replacement form one atomic group edit.
    @Suppress("LongMethod")
    fun update(
        actor: Actor,
        input: GroupUpdate,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        if (!administrator.isAdministrator) throw AccessDenied()

        database.commitTransaction {
            if (SuperGroupsTable.selectAll().where { SuperGroupsTable.id eq input.superGroupId.value }.count() != 1L) {
                throw OrganizationNotFound("Super group does not exist")
            }
            val now = LocalDateTime.now(ZoneOffset.UTC)
            val changed =
                GroupsTable.update(
                    where = {
                        (GroupsTable.id eq input.groupId.value) and
                            GroupsTable.version.matchesStoredVersion(input.expectedVersion)
                    },
                ) {
                    it[name] = input.name.value
                    it[prettyName] = input.prettyName.value
                    it[superGroupId] = input.superGroupId.value
                    it[version] = input.expectedVersion + 1
                    it[updatedAt] = now
                }
            if (changed != 1) throw OrganizationConflict("Group is missing or has been changed")

            MembershipsTable.deleteWhere { groupId eq input.groupId.value }
            for (membership in input.memberships) {
                MembershipsTable.insert {
                    it[userId] = membership.userId.value
                    it[groupId] = input.groupId.value
                    it[postId] = membership.postId.value
                    it[unofficialPostName] = membership.unofficialPostName.value
                    it[createdAt] = now
                }
            }
        }
    }
}
