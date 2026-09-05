package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll

class DeleteOwnedOAuthClients(
    private val database: DatabaseFactory,
) {
    /** Participates after the caller has authorized deletion and locked the owning user. */
    fun deleteIn(
        transaction: JdbcTransaction,
        userId: UserId,
    ): Set<OAuthApiKeyId> {
        database.requireTransaction(transaction)
        val clients =
            ClientsTable
                .selectAll()
                .where { (ClientsTable.official eq false) and (ClientsTable.createdBy eq userId.value) }
                .orderBy(ClientsTable.uid, SortOrder.ASC)
                .forUpdate()
                .map { ClientUid(it[ClientsTable.uid]) }
        return clients.mapNotNull { transaction.deleteClientRows(transaction.lockClientForDeletion(it)) }.toSet()
    }
}
