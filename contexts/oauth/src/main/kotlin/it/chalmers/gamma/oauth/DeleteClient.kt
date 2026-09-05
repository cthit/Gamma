package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as OAuthTextsTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID

class DeleteClient(
    private val database: DatabaseFactory,
) {
    /** Lock the current owner before the application decides whether deletion is allowed. */
    fun lockIn(
        transaction: JdbcTransaction,
        uid: ClientUid,
    ): LockedClientDeletion {
        database.requireTransaction(transaction)
        return transaction.lockClientForDeletion(uid)
    }

    /** Participates in the same transaction as authorization and linked API-key deletion. */
    fun deleteIn(
        transaction: JdbcTransaction,
        target: LockedClientDeletion,
    ): OAuthApiKeyId? {
        database.requireTransaction(transaction)
        return transaction.deleteClientRows(target)
    }
}

class LockedClientDeletion internal constructor(
    internal val transaction: JdbcTransaction,
    internal val uid: ClientUid,
    val owner: ClientOwner,
    internal val descriptionId: UUID?,
) {
    override fun toString(): String = "LockedClientDeletion(<redacted>)"
}

internal fun JdbcTransaction.lockClientForDeletion(uid: ClientUid): LockedClientDeletion {
    val row =
        ClientsTable
            .select(ClientsTable.descriptionId, ClientsTable.official, ClientsTable.createdBy)
            .where { ClientsTable.uid eq uid.value }
            // Locking this row is the deletion linearization point. A competing delete
            // waits here and then observes that the committed row no longer exists.
            .forUpdate()
            .limit(1)
            .firstOrNull()
            ?: throw OAuthClientNotFound("Client does not exist")
    val owner =
        if (row[ClientsTable.official]) {
            ClientOwner.Official
        } else {
            ClientOwner.User(UserId(checkNotNull(row[ClientsTable.createdBy])))
        }
    return LockedClientDeletion(this, uid, owner, row[ClientsTable.descriptionId])
}

internal fun JdbcTransaction.deleteClientRows(target: LockedClientDeletion): OAuthApiKeyId? {
    check(target.transaction === this) { "Client deletion must use the transaction that locked it" }
    val uid = target.uid
    val textId = target.descriptionId
    val apiKeyRow =
        ClientApiKeysTable
            .selectAll()
            .where { ClientApiKeysTable.clientUid eq uid.value }
            .limit(1)
            .firstOrNull()
    val apiKeyId = apiKeyRow?.get(ClientApiKeysTable.apiKeyId)
    ClientAuthorityUsersTable.deleteWhere { ClientAuthorityUsersTable.clientUid eq uid.value }
    ClientAuthoritySuperGroupsTable.deleteWhere { ClientAuthoritySuperGroupsTable.clientUid eq uid.value }
    ClientAuthoritiesTable.deleteWhere { ClientAuthoritiesTable.clientUid eq uid.value }
    ClientRestrictionSuperGroupsTable.deleteWhere {
        ClientRestrictionSuperGroupsTable.restrictionId eq uid.value
    }
    ClientRestrictionsTable.deleteWhere { ClientRestrictionsTable.clientUid eq uid.value }
    ClientApiKeysTable.deleteWhere { ClientApiKeysTable.clientUid eq uid.value }
    ClientScopesTable.deleteWhere { ClientScopesTable.clientUid eq uid.value }
    if (ClientsTable.deleteWhere { ClientsTable.uid eq uid.value } != 1) {
        throw OAuthClientNotFound("Client does not exist")
    }
    if (textId != null) OAuthTextsTable.deleteWhere { OAuthTextsTable.id eq textId }
    return apiKeyId?.let(::OAuthApiKeyId)
}
