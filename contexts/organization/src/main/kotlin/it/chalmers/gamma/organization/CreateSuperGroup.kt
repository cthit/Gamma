package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import org.jetbrains.exposed.v1.jdbc.insert
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class CreateSuperGroup(
    private val database: DatabaseFactory,
) {
    fun create(
        actor: Actor,
        input: NewSuperGroup,
    ): SuperGroupId {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        if (!administrator.isAdministrator) throw AccessDenied()

        return database.commitTransaction {
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
}
