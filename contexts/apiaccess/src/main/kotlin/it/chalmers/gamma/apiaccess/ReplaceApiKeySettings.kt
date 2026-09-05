package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.matchesStoredVersion
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDateTime
import java.time.ZoneOffset

class ReplaceApiKeySettings(
    private val database: DatabaseFactory,
) {
    /** Participates in the application operation after its current administrator check. */
    @Suppress("TooGenericExceptionThrown") // Retain the settings endpoint's existing wrong-key-type failure contract.
    fun replaceIn(
        transaction: JdbcTransaction,
        id: ApiKeyId,
        settings: ApiKeySettingsUpdate,
    ) {
        database.requireTransaction(transaction)
        val expectedType: ApiKeyType
        val superGroupTypes: List<SuperGroupTypeSetting>
        when (settings) {
            is ApiKeyInfoSettings -> {
                expectedType = ApiKeyType.INFO
                superGroupTypes = settings.superGroupTypes.map { SuperGroupTypeSetting(it, requiresManaged = false) }
            }

            is ApiKeyAccountScaffoldSettings -> {
                expectedType = ApiKeyType.ACCOUNT_SCAFFOLD
                superGroupTypes = settings.superGroupTypes
            }
        }
        val key =
            ApiKeysTable
                .selectAll()
                .where { ApiKeysTable.id eq id.value }
                .forUpdate()
                .limit(1)
                .firstOrNull()
                ?: throw ApiAccessNotFound("API key does not exist")
        if (key[ApiKeysTable.type] != expectedType.name) throw RuntimeException("Unexpected api key type")
        val rows =
            ApiKeySettingsTable
                .selectAll()
                .where { ApiKeySettingsTable.apiKeyId eq id.value }
                .forUpdate()
                .limit(2)
                .toList()
        check(rows.size <= 1) { "Multiple API key settings rows exist for one API key" }
        val row = rows.firstOrNull() ?: throw ApiAccessNotFound("API key settings do not exist")
        if ((row[ApiKeySettingsTable.version] ?: 0) != settings.version) {
            throw ApiAccessConflict("Settings are missing or have been changed")
        }
        val settingsId = row[ApiKeySettingsTable.id]
        val now = LocalDateTime.now(ZoneOffset.UTC)
        ApiKeyTypesTable.deleteWhere { ApiKeyTypesTable.settingsId eq settingsId }
        ManagedApiKeyTypesTable.deleteWhere { ManagedApiKeyTypesTable.settingsId eq settingsId }
        for (setting in superGroupTypes.distinctBy { it.type }) {
            ApiKeyTypesTable.insert {
                it[ApiKeyTypesTable.settingsId] = settingsId
                it[createdAt] = now
                it[type] = setting.type.value
            }
            if (setting.requiresManaged) {
                ManagedApiKeyTypesTable.insert {
                    it[ManagedApiKeyTypesTable.settingsId] = settingsId
                    it[createdAt] = now
                    it[type] = setting.type.value
                }
            }
        }
        val changed =
            ApiKeySettingsTable.update({
                (ApiKeySettingsTable.id eq settingsId) and
                    ApiKeySettingsTable.version.matchesStoredVersion(settings.version)
            }) {
                it[version] = settings.version + 1
                it[updatedAt] = now
            }
        if (changed != 1) throw ApiAccessConflict("Settings are missing or have been changed")
    }
}
