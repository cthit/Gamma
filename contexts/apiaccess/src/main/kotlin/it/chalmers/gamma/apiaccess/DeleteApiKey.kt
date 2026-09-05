package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as ApiTextsTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll

class DeleteApiKey(
    private val database: DatabaseFactory,
) {
    fun deleteIn(
        transaction: JdbcTransaction,
        id: ApiKeyId,
    ) {
        database.requireTransaction(transaction)
        if (!transaction.deleteApiKeyRow(id)) throw ApiAccessNotFound("API key does not exist")
    }
}

internal fun JdbcTransaction.deleteApiKeyRow(id: ApiKeyId): Boolean {
    val row =
        ApiKeysTable
            .selectAll()
            .where { ApiKeysTable.id eq id.value }
            .forUpdate()
            .limit(1)
            .firstOrNull()
            ?: return false
    val descriptionId = row[ApiKeysTable.descriptionId]
    val settingsId = loadUniqueApiKeySettingsRow(id)?.get(ApiKeySettingsTable.id)
    if (settingsId != null) {
        ManagedApiKeyTypesTable.deleteWhere { ManagedApiKeyTypesTable.settingsId eq settingsId }
        ApiKeyTypesTable.deleteWhere { ApiKeyTypesTable.settingsId eq settingsId }
        ApiKeySettingsTable.deleteWhere { ApiKeySettingsTable.id eq settingsId }
    }
    if (ApiKeysTable.deleteWhere { ApiKeysTable.id eq id.value } != 1) {
        // Absence before locking is idempotent cleanup. A row that survives this
        // DELETE is a failure and must roll back every participant's changes.
        throw ApiAccessNotFound("API key could not be deleted")
    }
    if (descriptionId != null) ApiTextsTable.deleteWhere { ApiTextsTable.id eq descriptionId }
    return true
}
