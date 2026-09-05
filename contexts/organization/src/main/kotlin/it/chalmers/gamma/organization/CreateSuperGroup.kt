package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import org.jetbrains.exposed.v1.jdbc.insert
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class CreateSuperGroup(
    private val database: DatabaseFactory,
    private val access: OrganizationAccess,
) {
    fun create(
        actor: Actor,
        input: NewSuperGroup,
    ): SuperGroupId =
        database.commitTransaction {
            access.requireAdministratorIn(this, actor)
            val superGroupId = SuperGroupId.generate()
            val textId = UUID.randomUUID()
            val now = LocalDateTime.now(ZoneOffset.UTC)
            LocalizedTextsTable.insert {
                it[id] = textId
                it[sv] = input.description.sv.value
                it[en] = input.description.en.value
                it[createdAt] = now
            }
            SuperGroupsTable.insert {
                it[id] = superGroupId.value
                it[name] = input.name.value
                it[prettyName] = input.prettyName.value
                it[type] = input.type.value
                it[descriptionId] = textId
                it[version] = 0
                it[createdAt] = now
                it[updatedAt] = now
            }
            superGroupId
        }
}
