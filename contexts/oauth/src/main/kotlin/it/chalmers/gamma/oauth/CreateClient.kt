package it.chalmers.gamma.oauth

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as OAuthTextsTable
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64
import java.util.UUID

class CreateClient(
    private val database: DatabaseFactory,
    private val bcryptCost: Int = 12,
    private val random: SecureRandom = SecureRandom(),
) {
    init {
        require(bcryptCost in 10..16)
    }

    fun prepare(input: NewOAuthClient): PreparedOAuthClient {
        check(
            TransactionManager.currentOrNull() == null,
        ) { "Client credential preparation requires no active transaction" }
        val frozen = input.copy(restrictedSuperGroupIds = input.restrictedSuperGroupIds.toSet())
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val clientId = ClientId(buildString(30) { repeat(30) { append(alphabet[random.nextInt(alphabet.length)]) } })
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        val secret = RawClientSecret(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
        val stored = "{bcrypt}" + BCrypt.withDefaults().hashToString(bcryptCost, secret.value.toCharArray())
        return PreparedOAuthClient(ClientUid.generate(), clientId, frozen, secret, stored, UUID.randomUUID())
    }

    /** All context-owned rows participate in the caller's authorized transaction. */
    fun insertIn(
        transaction: JdbcTransaction,
        prepared: PreparedOAuthClient,
        apiCredential: ClientApiCredential? = null,
    ): CreatedOAuthClient {
        database.requireTransaction(transaction)
        val input = prepared.input
        val uid = prepared.uid
        val now = LocalDateTime.now(ZoneOffset.UTC)
        OAuthTextsTable.insert {
            it[id] = prepared.descriptionId
            it[sv] = input.description.sv.value
            it[en] = input.description.en.value
            it[createdAt] = now
        }
        ClientsTable.insert {
            it[ClientsTable.uid] = uid.value
            it[clientId] = prepared.clientId.value
            it[secret] = prepared.storedSecret
            it[redirectUri] = input.redirectUri.value
            it[name] = input.name.value
            it[createdAt] = now
            it[descriptionId] = prepared.descriptionId
            it[official] = input.owner is ClientOwner.Official
            it[createdBy] = (input.owner as? ClientOwner.User)?.userId?.value
        }
        val scopes = mutableSetOf(Scope.OPENID, Scope.PROFILE)
        if (input.includeEmailScope) scopes.add(Scope.EMAIL)
        for (scope in scopes) {
            // OPENID is implicit in the protocol model and has never been stored in g_client_scope.
            if (scope == Scope.OPENID) continue
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
            // The restriction FK targets the client-keyed primary key, rather than the restriction id.
            ClientRestrictionsTable.insert {
                it[createdAt] = now
                it[restrictionId] = uid.value
                it[clientUid] = uid.value
            }
            for (superGroupId in input.restrictedSuperGroupIds.sorted()) {
                ClientRestrictionSuperGroupsTable.insert {
                    it[createdAt] = now
                    it[ClientRestrictionSuperGroupsTable.superGroupId] = superGroupId
                    it[restrictionId] = uid.value
                }
            }
        }
        return CreatedOAuthClient(
            OAuthClient(
                uid,
                prepared.clientId,
                input.redirectUri,
                input.name,
                input.description,
                scopes.toSet(),
                input.owner,
                apiCredential?.id,
                input.restrictedSuperGroupIds,
            ),
            prepared.secret,
            apiCredential,
        )
    }
}

class PreparedOAuthClient internal constructor(
    val uid: ClientUid,
    val clientId: ClientId,
    internal val input: NewOAuthClient,
    val secret: RawClientSecret,
    internal val storedSecret: String,
    internal val descriptionId: UUID,
) {
    override fun toString(): String = "PreparedOAuthClient(<redacted>)"
}
