package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere

class DeleteGroup(
    private val database: DatabaseFactory,
    private val access: OrganizationAccess,
) {
    fun delete(
        actor: Actor,
        groupId: GroupId,
    ) {
        database.commitTransaction {
            access.requireAdministratorIn(this, actor)
            // PostgreSQL cascades memberships and image pointers in the same transaction.
            if (GroupsTable.deleteWhere { id eq groupId.value } != 1) {
                throw OrganizationNotFound("Group does not exist")
            }
        }
    }
}
