package it.chalmers.gamma.oauth

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as OAuthTextsTable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.TextColumnType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64
import java.util.UUID

class OAuthClientStore(
    private val database: DatabaseFactory,
    private val bcryptCost: Int = 12,
    private val random: SecureRandom = SecureRandom(),
) {
    init {
        require(bcryptCost in 10..16)
    }

    fun findClient(uid: ClientUid): OAuthClient? =
        database.transaction(readOnly = true) {
            loadClients(setOf(uid)).firstOrNull()
        }

    fun findClient(clientId: ClientId): OAuthClient? =
        database.transaction(readOnly = true) {
            val uid = findClientUid(clientId) ?: return@transaction null
            loadClients(setOf(uid)).firstOrNull()
        }

    fun serverClient(uid: ClientUid): OAuthServerClient? =
        database.transaction(readOnly = true) {
            val client = loadClients(setOf(uid)).firstOrNull() ?: return@transaction null
            val secret =
                ClientsTable
                    .selectAll()
                    .where { ClientsTable.uid eq uid.value }
                    .limit(1)
                    .firstOrNull()
                    ?.get(ClientsTable.secret) ?: return@transaction null
            OAuthServerClient(client, secret)
        }

    fun serverClient(clientId: ClientId): OAuthServerClient? =
        database.transaction(readOnly = true) {
            val uid = findClientUid(clientId) ?: return@transaction null
            val client = loadClients(setOf(uid)).firstOrNull() ?: return@transaction null
            val secret =
                ClientsTable
                    .selectAll()
                    .where { ClientsTable.clientId eq clientId.value }
                    .limit(1)
                    .firstOrNull()
                    ?.get(ClientsTable.secret) ?: return@transaction null
            OAuthServerClient(client, secret)
        }

    fun listClients(owner: UserId? = null): List<OAuthClient> =
        database.transaction(readOnly = true) {
            val clientUids =
                owner?.let { userId ->
                    ClientsTable
                        .selectAll()
                        .where { (ClientsTable.official eq false) and (ClientsTable.createdBy eq userId.value) }
                        .map { ClientUid(it[ClientsTable.uid]) }
                        .toSet()
                }
            loadClients(clientUids)
        }

    fun authenticate(
        clientId: ClientId,
        secret: RawClientSecret,
    ): OAuthClient? {
        val hash =
            database.transaction(readOnly = true) {
                ClientsTable
                    .selectAll()
                    .where { ClientsTable.clientId eq clientId.value }
                    .limit(1)
                    .firstOrNull()
                    ?.get(ClientsTable.secret)
            } ?: return null
        if (!BCrypt
                .verifyer()
                .verify(
                    secret.value.toCharArray(),
                    hash.removePrefix("{bcrypt}").toCharArray(),
                ).verified
        ) {
            return null
        }
        return findClient(clientId)
    }

    fun isApproved(
        userId: UserId,
        clientUid: ClientUid,
    ): Boolean =
        database.transaction(readOnly = true) {
            UserApprovalsTable
                .selectAll()
                .where {
                    (UserApprovalsTable.userId eq userId.value) and
                        (UserApprovalsTable.clientUid eq clientUid.value)
                }.count() == 1L
        }

    fun approvedClients(userId: UserId): List<OAuthClient> =
        database.transaction(readOnly = true) {
            val ids =
                UserApprovalsTable
                    .selectAll()
                    .where { UserApprovalsTable.userId eq userId.value }
                    .map { ClientUid(it[UserApprovalsTable.clientUid]) }
                    .toSet()
            loadClients(ids)
        }

    fun approvedUserIds(clientUid: ClientUid): List<UserId> =
        database.transaction(readOnly = true) {
            UserApprovalsTable
                .selectAll()
                .where { UserApprovalsTable.clientUid eq clientUid.value }
                .map { UserId(it[UserApprovalsTable.userId]) }
        }

    fun findClientByApiKey(apiKeyId: OAuthApiKeyId): OAuthClient? =
        database.transaction(readOnly = true) {
            val uid =
                ClientApiKeysTable
                    .selectAll()
                    .where { ClientApiKeysTable.apiKeyId eq apiKeyId.value }
                    .limit(1)
                    .firstOrNull()
                    ?.get(ClientApiKeysTable.clientUid)
                    ?: return@transaction null
            loadClients(setOf(ClientUid(uid))).firstOrNull()
        }

    fun authorities(clientUid: ClientUid): List<ClientAuthority> =
        database.transaction(readOnly = true) {
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
            ClientAuthoritiesTable
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

    fun authoritiesForUser(
        clientUid: ClientUid,
        userId: UserId,
    ): List<AuthorityName> =
        database.transaction(readOnly = true) {
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
            direct.sorted().map(::AuthorityName)
        }

    fun authoritiesForUsers(
        clientUid: ClientUid,
        userIds: Set<UserId>,
    ): Map<UserId, List<AuthorityName>> {
        if (userIds.isEmpty()) return emptyMap()
        return database.transaction(readOnly = true) {
            ClientAuthorityUsersTable
                .selectAll()
                .where {
                    (ClientAuthorityUsersTable.clientUid eq clientUid.value) and
                        (ClientAuthorityUsersTable.userId inList userIds.map(UserId::value))
                }.groupBy(
                    { UserId(it[ClientAuthorityUsersTable.userId]) },
                    { AuthorityName(it[ClientAuthorityUsersTable.authorityName]) },
                ).mapValues { (_, authorities) -> authorities.distinct().sortedBy(AuthorityName::value) }
        }
    }

    fun createClient(
        input: NewOAuthClient,
        apiCredential: ClientApiCredential? = null,
    ): CreatedOAuthClient =
        database.transaction {
            val uid = ClientUid.generate()
            val clientId = generateClientId()
            val rawSecret = generateSecret()
            val textId = UUID.randomUUID()
            val now = now()
            OAuthTextsTable.insert {
                it[id] = textId
                it[sv] = input.description.sv.value
                it[en] = input.description.en.value
                it[createdAt] = now
            }
            ClientsTable.insert {
                it[ClientsTable.uid] = uid.value
                it[ClientsTable.clientId] = clientId.value
                it[secret] = hash(rawSecret)
                it[redirectUri] = input.redirectUri.value
                it[name] = input.name.value
                it[createdAt] = now
                it[descriptionId] = textId
                it[official] = input.owner is ClientOwner.Official
                it[createdBy] = (input.owner as? ClientOwner.User)?.userId?.value
            }
            val scopes =
                buildSet {
                    add(Scope.OPENID)
                    add(Scope.PROFILE)
                    if (input.includeEmailScope) add(Scope.EMAIL)
                }
            scopes.filterNot { it == Scope.OPENID }.forEach { scope ->
                ClientScopesTable.insert {
                    it[clientUid] = uid.value
                    it[ClientScopesTable.scope] = scope.name
                    it[createdAt] = now
                }
            }
            if (apiCredential != null) {
                ClientApiKeysTable.insert {
                    it[createdAt] = now
                    it[clientUid] = uid.value
                    it[apiKeyId] = apiCredential.id.value
                }
            }
            if (input.restrictedSuperGroupIds.isNotEmpty()) {
                // The restriction FK targets the client-keyed primary key rather than the restriction id.
                ClientRestrictionsTable.insert {
                    it[createdAt] = now
                    it[restrictionId] = uid.value
                    it[clientUid] = uid.value
                }
                input.restrictedSuperGroupIds.forEach { superGroupId ->
                    ClientRestrictionSuperGroupsTable.insert {
                        it[createdAt] = now
                        it[ClientRestrictionSuperGroupsTable.superGroupId] = superGroupId
                        it[restrictionId] = uid.value
                    }
                }
            }
            CreatedOAuthClient(
                OAuthClient(
                    uid,
                    clientId,
                    input.redirectUri,
                    input.name,
                    input.description,
                    scopes,
                    input.owner,
                    apiCredential?.id,
                    input.restrictedSuperGroupIds,
                ),
                rawSecret,
                apiCredential,
            )
        }

    fun resetSecret(uid: ClientUid): RawClientSecret =
        database.transaction {
            // Competing rotations must fail instead of serializing: otherwise an earlier caller
            // could receive a secret that a waiting rotation immediately supersedes.
            val acquired =
                exec(
                    "SELECT pg_try_advisory_xact_lock(hashtextextended(?::text, 0))",
                    listOf(TextColumnType() to uid.value.toString()),
                ) { result -> result.next() && result.getBoolean(1) } == true
            if (!acquired) throw OAuthClientConflict("Client credentials are already being changed")
            val secret = generateSecret()
            if (ClientsTable.update({ ClientsTable.uid eq uid.value }) {
                    it[ClientsTable.secret] = hash(secret)
                } != 1
            ) {
                throw OAuthClientNotFound("Client does not exist")
            }
            secret
        }

    fun approve(
        userId: UserId,
        clientUid: ClientUid,
    ) {
        database.transaction {
            if (!isApprovedInTransaction(userId, clientUid)) {
                require(ClientsTable.selectAll().where { ClientsTable.uid eq clientUid.value }.count() == 1L) {
                    "Client does not exist"
                }
                UserApprovalsTable.insertIgnore {
                    it[createdAt] = now()
                    it[UserApprovalsTable.userId] = userId.value
                    it[UserApprovalsTable.clientUid] = clientUid.value
                }
            }
        }
    }

    fun revokeApproval(
        userId: UserId,
        clientUid: ClientUid,
    ) {
        database.transaction {
            UserApprovalsTable.deleteWhere {
                (UserApprovalsTable.userId eq userId.value) and
                    (UserApprovalsTable.clientUid eq clientUid.value)
            }
        }
    }

    fun deleteClient(uid: ClientUid): OAuthApiKeyId? =
        database.transaction {
            val row =
                ClientsTable
                    .selectAll()
                    .where { ClientsTable.uid eq uid.value }
                    // Locking this row is the deletion linearization point. A competing delete
                    // waits here and then observes that the committed row no longer exists.
                    .forUpdate()
                    .limit(1)
                    .firstOrNull()
                    ?: throw OAuthClientNotFound("Client does not exist")
            val textId = row[ClientsTable.descriptionId]
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
            apiKeyId?.let(::OAuthApiKeyId)
        }

    fun deleteOwnedBy(userId: UserId): Set<OAuthApiKeyId> =
        database.transaction {
            val ownedClientUids =
                ClientsTable
                    .selectAll()
                    .where { (ClientsTable.official eq false) and (ClientsTable.createdBy eq userId.value) }
                    .map { ClientUid(it[ClientsTable.uid]) }
            ownedClientUids.mapNotNull { deleteClient(it) }.toSet()
        }

    fun createAuthority(
        clientUid: ClientUid,
        name: AuthorityName,
        userIds: Set<UserId> = emptySet(),
        superGroupIds: Set<UUID> = emptySet(),
    ) {
        database.transaction {
            require(ClientsTable.selectAll().where { ClientsTable.uid eq clientUid.value }.count() == 1L) {
                "Client does not exist"
            }
            val exists =
                ClientAuthoritiesTable
                    .selectAll()
                    .where {
                        (ClientAuthoritiesTable.clientUid eq clientUid.value) and
                            (ClientAuthoritiesTable.name eq name.value)
                    }.count() == 1L
            require(!exists) { "Authority already exists" }
            val now = now()
            ClientAuthoritiesTable.insert {
                it[createdAt] = now
                it[ClientAuthoritiesTable.clientUid] = clientUid.value
                it[ClientAuthoritiesTable.name] = name.value
            }
            userIds.forEach { userId ->
                ClientAuthorityUsersTable.insert {
                    it[createdAt] = now
                    it[ClientAuthorityUsersTable.userId] = userId.value
                    it[ClientAuthorityUsersTable.clientUid] = clientUid.value
                    it[authorityName] = name.value
                }
            }
            superGroupIds.forEach { superGroupId ->
                ClientAuthoritySuperGroupsTable.insert {
                    it[createdAt] = now
                    it[ClientAuthoritySuperGroupsTable.superGroupId] = superGroupId
                    it[ClientAuthoritySuperGroupsTable.clientUid] = clientUid.value
                    it[authorityName] = name.value
                }
            }
        }
    }

    fun deleteAuthority(
        clientUid: ClientUid,
        name: AuthorityName,
    ) {
        database.transaction {
            ClientAuthorityUsersTable.deleteWhere {
                (ClientAuthorityUsersTable.clientUid eq clientUid.value) and
                    (ClientAuthorityUsersTable.authorityName eq name.value)
            }
            ClientAuthoritySuperGroupsTable.deleteWhere {
                (ClientAuthoritySuperGroupsTable.clientUid eq clientUid.value) and
                    (ClientAuthoritySuperGroupsTable.authorityName eq name.value)
            }
            val changed =
                ClientAuthoritiesTable.deleteWhere {
                    (ClientAuthoritiesTable.clientUid eq clientUid.value) and
                        (ClientAuthoritiesTable.name eq name.value)
                }
            if (changed != 1) throw OAuthClientNotFound("Authority does not exist")
        }
    }

    private fun org.jetbrains.exposed.v1.jdbc.JdbcTransaction.isApprovedInTransaction(
        userId: UserId,
        clientUid: ClientUid,
    ): Boolean =
        UserApprovalsTable
            .selectAll()
            .where {
                (UserApprovalsTable.userId eq userId.value) and
                    (UserApprovalsTable.clientUid eq clientUid.value)
            }.count() == 1L

    private fun generateClientId(): ClientId {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return ClientId(buildString(30) { repeat(30) { append(alphabet[random.nextInt(alphabet.length)]) } })
    }

    private fun generateSecret(): RawClientSecret {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return RawClientSecret(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
    }

    private fun hash(secret: RawClientSecret): String =
        "{bcrypt}" + BCrypt.withDefaults().hashToString(bcryptCost, secret.value.toCharArray())

    private fun now(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
}
