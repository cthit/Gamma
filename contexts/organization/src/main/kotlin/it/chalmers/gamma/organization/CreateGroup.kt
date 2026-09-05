package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.LocalDateTime
import java.time.ZoneOffset

class CreateGroup(
    private val database: DatabaseFactory,
) {
    fun create(
        actor: Actor,
        input: NewGroup,
        memberships: List<NewGroupMembership>,
    ): GroupId {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        if (!administrator.isAdministrator) throw AccessDenied()

        return database.commitTransaction {
            if (SuperGroupsTable.selectAll().where { SuperGroupsTable.id eq input.superGroupId.value }.count() != 1L) {
                throw OrganizationNotFound("Super group does not exist")
            }
            val groupId = GroupId.generate()
            val now = LocalDateTime.now(ZoneOffset.UTC)
            GroupsTable.insert {
                it[id] = groupId.value
                it[name] = input.name.value
                it[prettyName] = input.prettyName.value
                it[superGroupId] = input.superGroupId.value
                it[version] = 0
                it[createdAt] = now
                it[updatedAt] = now
            }
            for (membership in memberships) {
                MembershipsTable.insert {
                    it[userId] = membership.userId.value
                    it[MembershipsTable.groupId] = groupId.value
                    it[postId] = membership.postId.value
                    it[unofficialPostName] = membership.unofficialPostName.value
                    it[createdAt] = now
                }
            }
            groupId
        }
    }
}
