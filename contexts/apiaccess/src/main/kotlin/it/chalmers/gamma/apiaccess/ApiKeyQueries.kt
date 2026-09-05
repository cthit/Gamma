package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.SuperGroupType
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll

class ApiKeyQueries(
    private val database: DatabaseFactory,
) {
    fun findApiKeyIn(
        transaction: JdbcTransaction,
        id: ApiKeyId,
    ): ApiKey? {
        database.requireTransaction(transaction)
        return apiKeysWithDescription()
            .selectAll()
            .where { ApiKeysTable.id eq id.value }
            .limit(1)
            .firstOrNull()
            ?.toApiKey()
    }

    fun listApiKeysIn(transaction: JdbcTransaction): List<ApiKey> {
        database.requireTransaction(transaction)
        return apiKeysWithDescription()
            .selectAll()
            .orderBy(ApiKeysTable.name, SortOrder.ASC)
            .map { it.toApiKey() }
    }

    fun infoSettingsIn(
        transaction: JdbcTransaction,
        id: ApiKeyId,
    ): ApiKeyInfoSettings? =
        storedSettingsIn(transaction, id, ApiKeyType.INFO)?.let { settings ->
            ApiKeyInfoSettings(
                version = settings.version,
                superGroupTypes = settings.superGroupTypes.map { it.type },
            )
        }

    fun accountScaffoldSettingsIn(
        transaction: JdbcTransaction,
        id: ApiKeyId,
    ): ApiKeyAccountScaffoldSettings? =
        storedSettingsIn(transaction, id, ApiKeyType.ACCOUNT_SCAFFOLD)?.let { settings ->
            ApiKeyAccountScaffoldSettings(settings.version, settings.superGroupTypes)
        }

    @Suppress("TooGenericExceptionThrown") // Preserve the legacy adapter's wrong-key-type contract.
    private fun storedSettingsIn(
        transaction: JdbcTransaction,
        id: ApiKeyId,
        expectedType: ApiKeyType,
    ): StoredApiKeySettings? {
        database.requireTransaction(transaction)
        val apiKey =
            ApiKeysTable
                .selectAll()
                .where { ApiKeysTable.id eq id.value }
                .limit(1)
                .firstOrNull()
                ?: return null
        val row = transaction.loadUniqueApiKeySettingsRow(id)
        val actualType = apiKey[ApiKeysTable.type]
        if (actualType != expectedType.name) throw RuntimeException("Unexpected api key type")
        if (row == null) return null
        val settingsId = row[ApiKeySettingsTable.id]
        val managed =
            ManagedApiKeyTypesTable
                .selectAll()
                .where { ManagedApiKeyTypesTable.settingsId eq settingsId }
                .map { it[ManagedApiKeyTypesTable.type] }
                .toSet()
        val types =
            ApiKeyTypesTable
                .selectAll()
                .where { ApiKeyTypesTable.settingsId eq settingsId }
                .orderBy(ApiKeyTypesTable.type, SortOrder.ASC)
                .map {
                    val type = it[ApiKeyTypesTable.type]
                    SuperGroupTypeSetting(SuperGroupType(type), type in managed)
                }
        return StoredApiKeySettings(row[ApiKeySettingsTable.version] ?: 0, types)
    }
}

private data class StoredApiKeySettings(
    val version: Int,
    val superGroupTypes: List<SuperGroupTypeSetting>,
)

internal fun JdbcTransaction.loadUniqueApiKeySettingsRow(id: ApiKeyId): ResultRow? {
    val rows =
        ApiKeySettingsTable
            .selectAll()
            .where { ApiKeySettingsTable.apiKeyId eq id.value }
            .limit(2)
            .toList()
    check(rows.size <= 1) { "Multiple API key settings rows exist for one API key" }
    return rows.firstOrNull()
}
