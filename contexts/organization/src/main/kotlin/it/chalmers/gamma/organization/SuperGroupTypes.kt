package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.LocalDateTime
import java.time.ZoneOffset

class SuperGroupTypes(
    private val database: DatabaseFactory,
    private val access: OrganizationAccess,
) {
    fun create(
        actor: Actor,
        type: SuperGroupType,
    ) {
        database.commitTransaction {
            access.requireAdministratorIn(this, actor)
            if (SuperGroupTypesTable.selectAll().where { SuperGroupTypesTable.name eq type.value }.count() != 0L) {
                throw OrganizationConflict("Super group type already exists")
            }
            SuperGroupTypesTable.insert {
                it[name] = type.value
                it[createdAt] = LocalDateTime.now(ZoneOffset.UTC)
            }
        }
    }

    fun delete(
        actor: Actor,
        type: SuperGroupType,
    ) {
        database.commitTransaction {
            access.requireAdministratorIn(this, actor)
            if (SuperGroupsTable.selectAll().where { SuperGroupsTable.type eq type.value }.count() != 0L) {
                throw OrganizationConflict("Super group type is still in use")
            }
            if (SuperGroupTypesTable.deleteWhere { name eq type.value } != 1) {
                throw OrganizationNotFound("Super group type does not exist")
            }
        }
    }
}
