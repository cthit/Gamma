package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction

class DeleteOwnedApiKeys(
    private val database: DatabaseFactory,
) {
    /** Participates after OAuth has removed the client links and identified its owned keys. */
    fun deleteIn(
        transaction: JdbcTransaction,
        ids: Set<ApiKeyId>,
    ) {
        database.requireTransaction(transaction)
        for (id in ids.sortedBy { it.value }) transaction.deleteApiKeyRow(id)
    }
}
