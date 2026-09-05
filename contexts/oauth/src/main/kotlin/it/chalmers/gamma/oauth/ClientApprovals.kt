package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import java.sql.Connection
import java.time.LocalDateTime
import java.time.ZoneOffset

class ClientApprovals(
    private val database: DatabaseFactory,
) {
    /** The caller has checked and locked the authenticated account in this transaction. */
    fun approveIn(
        transaction: JdbcTransaction,
        userId: UserId,
        clientUid: ClientUid,
        requestedScopes: Set<Scope>,
    ) {
        database.requireTransaction(transaction)
        require(requestedScopes.isNotEmpty()) { "OAuth consent must contain at least one scope" }
        val client =
            ClientsTable
                .select(ClientsTable.clientId)
                .where { ClientsTable.uid eq clientUid.value }
                .forUpdate()
                .firstOrNull()
        requireNotNull(client?.get(ClientsTable.clientId)?.let(::ClientId)) { "OAuth consent client does not exist" }
        // The parent lock prevents new scope rows, and these locks prevent changes
        // to existing rows between checking the complete scope set and recording approval.
        val scopes =
            ClientScopesTable
                .select(ClientScopesTable.scope)
                .where { ClientScopesTable.clientUid eq clientUid.value }
                .forUpdate()
                .mapTo(mutableSetOf(Scope.OPENID)) { row ->
                    Scope.entries.first { it.wireValue.equals(row[ClientScopesTable.scope], ignoreCase = true) }
                }
        require(scopes == requestedScopes) { "OAuth consent must cover the client's complete registered scope set" }
        val existing =
            UserApprovalsTable
                .select(UserApprovalsTable.userId)
                .where {
                    (UserApprovalsTable.userId eq userId.value) and
                        (UserApprovalsTable.clientUid eq clientUid.value)
                }.any()
        if (existing) return
        val inserted =
            UserApprovalsTable.insert {
                it[createdAt] = LocalDateTime.now(ZoneOffset.UTC)
                it[UserApprovalsTable.userId] = userId.value
                it[UserApprovalsTable.clientUid] = clientUid.value
            }
        check(inserted.insertedCount == 1) { "OAuth consent could not be saved" }
    }

    /** Complete idempotent revocation for an identity supplied by the authenticated HTTP/protocol adapter. */
    fun revoke(
        userId: UserId,
        clientUid: ClientUid,
    ) {
        database.commitTransaction {
            val client =
                ClientsTable
                    .select(ClientsTable.uid)
                    .where { ClientsTable.uid eq clientUid.value }
                    .forUpdate()
                    .firstOrNull()
                    ?: return@commitTransaction
            val changed =
                UserApprovalsTable.deleteWhere {
                    (UserApprovalsTable.userId eq userId.value) and
                        (UserApprovalsTable.clientUid eq client[ClientsTable.uid])
                }
            if (changed == 0) {
                check(
                    !UserApprovalsTable
                        .select(UserApprovalsTable.userId)
                        .where {
                            (UserApprovalsTable.userId eq userId.value) and
                                (UserApprovalsTable.clientUid eq clientUid.value)
                        }.any(),
                ) { "OAuth consent could not be revoked" }
            }
        }
    }

    fun approvedScopes(
        userId: UserId,
        clientUid: ClientUid,
    ): Set<Scope>? =
        database.commitTransaction(readOnly = true, isolationLevel = Connection.TRANSACTION_REPEATABLE_READ) {
            val approval =
                UserApprovalsTable
                    .join(
                        ClientsTable,
                        JoinType.INNER,
                        UserApprovalsTable.clientUid,
                        ClientsTable.uid,
                    ).select(ClientsTable.clientId)
                    .where {
                        (UserApprovalsTable.userId eq userId.value) and
                            (UserApprovalsTable.clientUid eq clientUid.value)
                    }.firstOrNull() ?: return@commitTransaction null
            approval[ClientsTable.clientId]?.let(::ClientId) ?: return@commitTransaction null
            ClientScopesTable
                .select(ClientScopesTable.scope)
                .where { ClientScopesTable.clientUid eq clientUid.value }
                .mapTo(mutableSetOf(Scope.OPENID)) { row ->
                    Scope.entries.first { it.wireValue.equals(row[ClientScopesTable.scope], ignoreCase = true) }
                }
        }
}
