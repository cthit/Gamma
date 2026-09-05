package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class ClientAuthorities(
    private val database: DatabaseFactory,
) {
    /** Lock current ownership before the application authorizes an authority change. */
    fun lockIn(
        transaction: JdbcTransaction,
        uid: ClientUid,
    ): LockedClientAuthorities {
        database.requireTransaction(transaction)
        val client =
            ClientsTable
                .select(ClientsTable.clientId, ClientsTable.official, ClientsTable.createdBy)
                .where { ClientsTable.uid eq uid.value }
                .forUpdate()
                .firstOrNull()
                ?: throw OAuthClientNotFound("Client does not exist")
        client[ClientsTable.clientId]?.let(::ClientId) ?: throw OAuthClientNotFound("Client does not exist")
        val owner =
            if (client[ClientsTable.official]) {
                ClientOwner.Official
            } else {
                ClientOwner.User(UserId(checkNotNull(client[ClientsTable.createdBy])))
            }
        return LockedClientAuthorities(transaction, uid, owner)
    }

    fun createIn(
        transaction: JdbcTransaction,
        target: LockedClientAuthorities,
        name: AuthorityName,
        userIds: Set<UserId>,
        superGroupIds: Set<UUID>,
    ) {
        database.requireTransaction(transaction)
        check(target.transaction === transaction) {
            "Authority creation requires the transaction that locked its client"
        }
        val uid = target.uid
        val exists =
            ClientAuthoritiesTable
                .select(ClientAuthoritiesTable.name)
                .where {
                    (ClientAuthoritiesTable.clientUid eq uid.value) and
                        (ClientAuthoritiesTable.name eq name.value)
                }.any()
        require(!exists) { "Authority already exists" }
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val authority =
            ClientAuthoritiesTable.insert {
                it[createdAt] = now
                it[clientUid] = uid.value
                it[ClientAuthoritiesTable.name] = name.value
            }
        check(authority.insertedCount == 1) { "Client authority could not be created" }
        for (userId in userIds.sortedBy(UserId::value)) {
            val assigned =
                ClientAuthorityUsersTable.insert {
                    it[createdAt] = now
                    it[ClientAuthorityUsersTable.userId] = userId.value
                    it[clientUid] = uid.value
                    it[authorityName] = name.value
                }
            check(assigned.insertedCount == 1) { "Client authority user could not be assigned" }
        }
        for (superGroupId in superGroupIds.sorted()) {
            val assigned =
                ClientAuthoritySuperGroupsTable.insert {
                    it[createdAt] = now
                    it[ClientAuthoritySuperGroupsTable.superGroupId] = superGroupId
                    it[clientUid] = uid.value
                    it[authorityName] = name.value
                }
            check(assigned.insertedCount == 1) { "Client authority super group could not be assigned" }
        }
    }

    fun deleteIn(
        transaction: JdbcTransaction,
        target: LockedClientAuthorities,
        name: AuthorityName,
    ) {
        database.requireTransaction(transaction)
        check(target.transaction === transaction) {
            "Authority deletion requires the transaction that locked its client"
        }
        val uid = target.uid
        ClientAuthorityUsersTable.deleteWhere {
            (clientUid eq uid.value) and (authorityName eq name.value)
        }
        ClientAuthoritySuperGroupsTable.deleteWhere {
            (clientUid eq uid.value) and (authorityName eq name.value)
        }
        val changed =
            ClientAuthoritiesTable.deleteWhere {
                (clientUid eq uid.value) and (ClientAuthoritiesTable.name eq name.value)
            }
        if (changed != 1) throw OAuthClientNotFound("Authority does not exist")
    }
}

class LockedClientAuthorities internal constructor(
    internal val transaction: JdbcTransaction,
    internal val uid: ClientUid,
    val owner: ClientOwner,
) {
    override fun toString(): String = "LockedClientAuthorities(<redacted>)"
}
