package it.chalmers.gamma.organization

import it.chalmers.gamma.platform.core.AccessDenied
import it.chalmers.gamma.platform.core.Actor
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as LocalizedTextsTable
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

data class SuperGroupUpdate(
    val superGroupId: SuperGroupId,
    val expectedVersion: Int,
    val name: OrganizationName,
    val prettyName: PrettyName,
    val type: SuperGroupType,
    val description: LocalizedText,
)

class UpdateSuperGroup(
    private val database: DatabaseFactory,
) {
    fun update(
        actor: Actor,
        input: SuperGroupUpdate,
    ) {
        val administrator = actor as? Actor.User ?: throw AccessDenied()
        if (!administrator.isAdministrator) throw AccessDenied()

        database.commitTransaction {
            val row =
                SuperGroupsTable
                    .selectAll()
                    .where {
                        (SuperGroupsTable.id eq input.superGroupId.value) and
                            SuperGroupsTable.version.matchesStoredVersion(input.expectedVersion)
                    }.limit(1)
                    .firstOrNull()
                    ?: throw OrganizationConflict("Super group is missing or has been changed")
            val descriptionId = row[SuperGroupsTable.descriptionId]
            val now = LocalDateTime.now(ZoneOffset.UTC)
            val changed =
                SuperGroupsTable.update(
                    where = {
                        (SuperGroupsTable.id eq input.superGroupId.value) and
                            SuperGroupsTable.version.matchesStoredVersion(input.expectedVersion)
                    },
                ) {
                    it[name] = input.name.value
                    it[prettyName] = input.prettyName.value
                    it[type] = input.type.value
                    it[version] = input.expectedVersion + 1
                    it[updatedAt] = now
                }
            if (changed != 1) throw OrganizationConflict("Super group is missing or has been changed")

            if (descriptionId == null) {
                val textId = UUID.randomUUID()
                LocalizedTextsTable.insert {
                    it[id] = textId
                    it[sv] = input.description.sv.value
                    it[en] = input.description.en.value
                    it[createdAt] = now
                }
                SuperGroupsTable.update({ SuperGroupsTable.id eq input.superGroupId.value }) {
                    it[SuperGroupsTable.descriptionId] = textId
                }
            } else {
                LocalizedTextsTable.update({ LocalizedTextsTable.id eq descriptionId }) {
                    it[sv] = input.description.sv.value
                    it[en] = input.description.en.value
                }
            }
        }
    }
}
