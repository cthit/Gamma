package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as OAuthTextsTable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.selectAll

internal fun JdbcTransaction.loadClients(clientUids: Set<ClientUid>? = null): List<OAuthClient> {
    if (clientUids != null && clientUids.isEmpty()) return emptyList()
    val rawClientUids = clientUids?.map(ClientUid::value)
    val persistedScopes =
        ClientScopesTable
            .selectAll()
            .let { query -> rawClientUids?.let { query.where { ClientScopesTable.clientUid inList it } } ?: query }
            .groupBy(
                { ClientUid(it[ClientScopesTable.clientUid]) },
                { row ->
                    val persistedScope = row[ClientScopesTable.scope]
                    Scope.entries.first { scope -> scope.wireValue.equals(persistedScope, ignoreCase = true) }
                },
            )
    val apiKeys =
        ClientApiKeysTable
            .selectAll()
            .let { query -> rawClientUids?.let { query.where { ClientApiKeysTable.clientUid inList it } } ?: query }
            .associate {
                ClientUid(it[ClientApiKeysTable.clientUid]) to OAuthApiKeyId(it[ClientApiKeysTable.apiKeyId])
            }
    val restrictions =
        ClientRestrictionSuperGroupsTable
            .selectAll()
            .let { query ->
                rawClientUids?.let { query.where { ClientRestrictionSuperGroupsTable.restrictionId inList it } }
                    ?: query
            }.groupBy(
                { ClientUid(it[ClientRestrictionSuperGroupsTable.restrictionId]) },
                { it[ClientRestrictionSuperGroupsTable.superGroupId] },
            )
    val clients =
        ClientsTable
            .join(
                OAuthTextsTable,
                JoinType.LEFT,
                ClientsTable.descriptionId,
                OAuthTextsTable.id,
            ).selectAll()
    return clients
        .let { query -> rawClientUids?.let { query.where { ClientsTable.uid inList it } } ?: query }
        .orderBy(ClientsTable.name, SortOrder.ASC)
        .mapNotNull { row ->
            val rawId = row[ClientsTable.clientId] ?: return@mapNotNull null
            val uid = ClientUid(row[ClientsTable.uid])
            OAuthClient(
                uid = uid,
                clientId = ClientId(rawId),
                redirectUri = RedirectUri(row[ClientsTable.redirectUri]),
                name = ClientName(row[ClientsTable.name]),
                description =
                    LocalizedText.of(
                        row.getOrNull(OAuthTextsTable.sv).orEmpty(),
                        row.getOrNull(OAuthTextsTable.en).orEmpty(),
                    ),
                scopes = setOf(Scope.OPENID) + persistedScopes[uid].orEmpty(),
                owner =
                    if (row[ClientsTable.official]) {
                        ClientOwner.Official
                    } else {
                        ClientOwner.User(UserId(checkNotNull(row[ClientsTable.createdBy])))
                    },
                apiKeyId = apiKeys[uid],
                restrictedSuperGroupIds = restrictions[uid].orEmpty().toSet(),
            )
        }
}

internal fun JdbcTransaction.loadClientAuthorities(clientUid: ClientUid): List<ClientAuthority> {
    val users =
        ClientAuthorityUsersTable
            .selectAll()
            .where { ClientAuthorityUsersTable.clientUid eq clientUid.value }
            .groupBy(
                { it[ClientAuthorityUsersTable.authorityName] },
                { UserId(it[ClientAuthorityUsersTable.userId]) },
            )
    val superGroups =
        ClientAuthoritySuperGroupsTable
            .selectAll()
            .where { ClientAuthoritySuperGroupsTable.clientUid eq clientUid.value }
            .groupBy(
                { it[ClientAuthoritySuperGroupsTable.authorityName] },
                { it[ClientAuthoritySuperGroupsTable.superGroupId] },
            )
    return ClientAuthoritiesTable
        .selectAll()
        .where { ClientAuthoritiesTable.clientUid eq clientUid.value }
        .orderBy(ClientAuthoritiesTable.name, SortOrder.ASC)
        .map {
            val name = it[ClientAuthoritiesTable.name]
            ClientAuthority(
                clientUid,
                AuthorityName(name),
                users[name].orEmpty().toSet(),
                superGroups[name].orEmpty().toSet(),
            )
        }
}
