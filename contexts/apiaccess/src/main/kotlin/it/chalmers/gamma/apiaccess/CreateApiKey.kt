package it.chalmers.gamma.apiaccess

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.platform.database.SharedLocalizedTextsTable as ApiTextsTable
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64
import java.util.UUID
import java.util.concurrent.CancellationException

class CreateApiKey(
    private val database: DatabaseFactory,
    private val verificationCache: ApiTokenVerificationCache = ApiTokenVerificationCache.Disabled,
    private val bcryptCost: Int = 12,
    private val random: SecureRandom = SecureRandom(),
) {
    init {
        require(bcryptCost in 10..16)
    }

    /** Complete issuance for trusted bootstrap and OAuth callers that own authorization. */
    fun create(
        name: ApiKeyName,
        description: LocalizedText,
        type: ApiKeyType,
    ): CreatedApiKey {
        val prepared = prepare(name, description, type)
        val created = database.commitTransaction { insertIn(this, prepared) }
        publishAfterCommit(prepared)
        return created
    }

    /** Credential work runs once, before a caller's retryable transaction. */
    fun prepare(
        name: ApiKeyName,
        description: LocalizedText,
        type: ApiKeyType,
    ): PreparedApiKey {
        check(
            TransactionManager.currentOrNull() == null,
        ) { "API credential preparation requires no active transaction" }
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        val token = RawApiToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
        val stored =
            StoredApiCredential("{bcrypt}" + BCrypt.withDefaults().hashToString(bcryptCost, token.value.toCharArray()))
        return PreparedApiKey(
            ApiKey(ApiKeyId.generate(), name, description, type, version = 0),
            token,
            stored,
            UUID.randomUUID(),
            UUID.randomUUID(),
        )
    }

    /** Participates after the caller has checked current authority; does not publish cache entries. */
    fun insertIn(
        transaction: JdbcTransaction,
        prepared: PreparedApiKey,
    ): CreatedApiKey {
        database.requireTransaction(transaction)
        val key = prepared.apiKey
        val now = LocalDateTime.now(ZoneOffset.UTC)
        ApiTextsTable.insert {
            it[id] = prepared.descriptionId
            it[sv] = key.description.sv.value
            it[en] = key.description.en.value
            it[createdAt] = now
        }
        ApiKeysTable.insert {
            it[id] = key.id.value
            it[name] = key.name.value
            it[token] = prepared.stored.value
            it[type] = key.type.name
            it[createdAt] = now
            it[updatedAt] = now
            it[version] = 0
            it[descriptionId] = prepared.descriptionId
        }
        if (key.type == ApiKeyType.INFO || key.type == ApiKeyType.ACCOUNT_SCAFFOLD) {
            ApiKeySettingsTable.insert {
                it[id] = prepared.settingsId
                it[createdAt] = now
                it[updatedAt] = now
                it[version] = 0
                it[apiKeyId] = key.id.value
            }
        }
        return CreatedApiKey(key, prepared.token)
    }

    @Suppress("TooGenericExceptionCaught") // The cache is optional; cancellation and interruption still propagate.
    fun publishAfterCommit(prepared: PreparedApiKey) {
        check(TransactionManager.currentOrNull() == null) { "API credential caching requires no active transaction" }
        try {
            verificationCache.remember(prepared.apiKey.id, prepared.stored, prepared.token)
        } catch (failure: CancellationException) {
            throw failure
        } catch (failure: InterruptedException) {
            throw failure
        } catch (_: Exception) {
            // The committed database credential remains authoritative during an ordinary cache outage.
        }
    }
}

/** Prepared values are reusable across SQL retries, but cannot be constructed or altered by callers. */
class PreparedApiKey internal constructor(
    val apiKey: ApiKey,
    val token: RawApiToken,
    internal val stored: StoredApiCredential,
    internal val descriptionId: UUID,
    internal val settingsId: UUID,
) {
    override fun toString(): String = "PreparedApiKey(<redacted>)"
}
