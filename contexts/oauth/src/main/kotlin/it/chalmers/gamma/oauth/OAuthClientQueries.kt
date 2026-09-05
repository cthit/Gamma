package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll

class OAuthClientQueries(
    private val database: DatabaseFactory,
) {
    fun findClientIn(
        transaction: JdbcTransaction,
        uid: ClientUid,
    ): OAuthClient? {
        database.requireTransaction(transaction)
        return transaction.loadClients(setOf(uid)).firstOrNull()
    }

    fun listClientsIn(
        transaction: JdbcTransaction,
        owner: UserId? = null,
    ): List<OAuthClient> {
        database.requireTransaction(transaction)
        val clientUids =
            owner?.let { userId ->
                ClientsTable
                    .selectAll()
                    .where { (ClientsTable.official eq false) and (ClientsTable.createdBy eq userId.value) }
                    .map { ClientUid(it[ClientsTable.uid]) }
                    .toSet()
            }
        return transaction.loadClients(clientUids)
    }

    fun approvedClientsIn(
        transaction: JdbcTransaction,
        userId: UserId,
    ): List<OAuthClient> {
        database.requireTransaction(transaction)
        val ids =
            UserApprovalsTable
                .selectAll()
                .where { UserApprovalsTable.userId eq userId.value }
                .map { ClientUid(it[UserApprovalsTable.clientUid]) }
                .toSet()
        return transaction.loadClients(ids)
    }

    fun approvedUserIdsIn(
        transaction: JdbcTransaction,
        clientUid: ClientUid,
    ): List<UserId> {
        database.requireTransaction(transaction)
        return UserApprovalsTable
            .selectAll()
            .where { UserApprovalsTable.clientUid eq clientUid.value }
            .map { UserId(it[UserApprovalsTable.userId]) }
    }

    fun findClientByApiKeyIn(
        transaction: JdbcTransaction,
        apiKeyId: OAuthApiKeyId,
    ): OAuthClient? {
        database.requireTransaction(transaction)
        val uid =
            ClientApiKeysTable
                .selectAll()
                .where { ClientApiKeysTable.apiKeyId eq apiKeyId.value }
                .limit(1)
                .firstOrNull()
                ?.get(ClientApiKeysTable.clientUid)
                ?: return null
        return transaction.loadClients(setOf(ClientUid(uid))).firstOrNull()
    }

    fun authoritiesIn(
        transaction: JdbcTransaction,
        clientUid: ClientUid,
    ): List<ClientAuthority> {
        database.requireTransaction(transaction)
        return transaction.loadClientAuthorities(clientUid)
    }

    fun authoritiesForUserIn(
        transaction: JdbcTransaction,
        clientUid: ClientUid,
        userId: UserId,
    ): List<AuthorityName> {
        database.requireTransaction(transaction)
        val direct =
            ClientAuthorityUsersTable
                .selectAll()
                .where {
                    (ClientAuthorityUsersTable.clientUid eq clientUid.value) and
                        (ClientAuthorityUsersTable.userId eq userId.value)
                }.map { it[ClientAuthorityUsersTable.authorityName] }
                .toMutableSet()
        // Super-group-derived authorities are resolved at the application boundary, where
        // organization membership is available without coupling this context to its tables.
        return direct.sorted().map(::AuthorityName)
    }
}
