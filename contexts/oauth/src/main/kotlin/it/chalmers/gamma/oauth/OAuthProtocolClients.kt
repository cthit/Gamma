package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.select
import java.sql.Connection
import java.util.UUID

class OAuthProtocolClients(
    private val database: DatabaseFactory,
) {
    fun serverClient(uid: ClientUid): OAuthServerClient? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val row =
                ClientsTable.select(ClientsTable.secret).where { ClientsTable.uid eq uid.value }.firstOrNull()
                    ?: return@commitTransaction null
            val client = loadClients(setOf(uid)).firstOrNull() ?: return@commitTransaction null
            OAuthServerClient(client, row[ClientsTable.secret])
        }

    fun serverClient(clientId: ClientId): OAuthServerClient? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val row =
                ClientsTable
                    .select(ClientsTable.uid, ClientsTable.secret)
                    .where { ClientsTable.clientId eq clientId.value }
                    .firstOrNull() ?: return@commitTransaction null
            val uid = ClientUid(row[ClientsTable.uid])
            val client = loadClients(setOf(uid)).firstOrNull() ?: return@commitTransaction null
            OAuthServerClient(client, row[ClientsTable.secret])
        }

    /** Null means the client is absent or hidden; an empty set means an existing unrestricted client. */
    fun restrictionsIn(
        transaction: JdbcTransaction,
        uid: ClientUid,
    ): Set<UUID>? {
        database.requireTransaction(transaction)
        val rows =
            ClientsTable
                .join(
                    ClientRestrictionSuperGroupsTable,
                    JoinType.LEFT,
                    ClientsTable.uid,
                    ClientRestrictionSuperGroupsTable.restrictionId,
                ).select(ClientsTable.uid, ClientRestrictionSuperGroupsTable.superGroupId)
                .where { (ClientsTable.uid eq uid.value) and ClientsTable.clientId.isNotNull() }
                .toList()
        if (rows.isEmpty()) return null
        return rows.mapNotNull { it.getOrNull(ClientRestrictionSuperGroupsTable.superGroupId) }.toSet()
    }

    /** Public consent metadata participates in its page owner's snapshot; credential hashes are not loaded. */
    fun consentDetailsIn(
        transaction: JdbcTransaction,
        clientId: ClientId,
    ): ClientConsentDetails? {
        database.requireTransaction(transaction)
        val row =
            ClientsTable
                .select(ClientsTable.uid, ClientsTable.name, ClientsTable.official, ClientsTable.createdBy)
                .where { ClientsTable.clientId eq clientId.value }
                .firstOrNull() ?: return null
        val uid = ClientUid(row[ClientsTable.uid])
        val scopes =
            ClientScopesTable
                .select(ClientScopesTable.scope)
                .where { ClientScopesTable.clientUid eq uid.value }
                .mapTo(mutableSetOf(Scope.OPENID)) { value ->
                    Scope.entries.first { it.wireValue.equals(value[ClientScopesTable.scope], ignoreCase = true) }
                }
        val owner =
            if (row[ClientsTable.official]) {
                ClientOwner.Official
            } else {
                ClientOwner.User(UserId(checkNotNull(row[ClientsTable.createdBy])))
            }
        return ClientConsentDetails(uid, ClientName(row[ClientsTable.name]), scopes, owner)
    }
}

data class ClientConsentDetails(
    val uid: ClientUid,
    val name: ClientName,
    val scopes: Set<Scope>,
    val owner: ClientOwner,
) {
    override fun toString(): String = "ClientConsentDetails(<redacted>)"
}
