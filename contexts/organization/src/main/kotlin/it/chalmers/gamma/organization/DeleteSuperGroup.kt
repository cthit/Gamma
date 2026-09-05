package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll

class DeleteSuperGroup(
    private val database: DatabaseFactory,
) {
    fun delete(
        actor: Actor,
        superGroupId: SuperGroupId,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        if (!administrator.isAdministrator) throw AccessDenied()

        database.commitTransaction {
            // Keep the description pointer stable while removing its owner and text.
            val row =
                SuperGroupsTable
                    .selectAll()
                    .where { SuperGroupsTable.id eq superGroupId.value }
                    .forUpdate()
                    .limit(1)
                    .firstOrNull()
                    ?: throw OrganizationNotFound("Super group does not exist")
            val descriptionId = row[SuperGroupsTable.descriptionId]
            if (GroupsTable.selectAll().where { GroupsTable.superGroupId eq superGroupId.value }.count() != 0L) {
                throw OrganizationConflict("Super group is still used by groups")
            }
            SuperGroupsTable.deleteWhere { id eq superGroupId.value }
            if (descriptionId != null) LocalizedTextsTable.deleteWhere { id eq descriptionId }
        }
    }
}
