package it.chalmers.gamma.apiaccess

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.update
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Base64
import java.util.concurrent.CancellationException

class RotateApiKey(
    private val database: DatabaseFactory,
    private val verificationCache: ApiTokenVerificationCache = ApiTokenVerificationCache.Disabled,
    private val bcryptCost: Int = 12,
    private val random: SecureRandom = SecureRandom(),
) {
    init {
        require(bcryptCost in 10..16)
    }

    /** Validate existence/type before expensive work, then prepare one credential for all SQL retries. */
    fun prepare(
        id: ApiKeyId,
        requiredType: ApiKeyType? = null,
    ): PreparedApiKeyRotation {
        val type =
            database.commitTransaction(readOnly = true) {
                val row =
                    ApiKeysTable.select(ApiKeysTable.type).where { ApiKeysTable.id eq id.value }.firstOrNull()
                        ?: throw ApiAccessNotFound("API key does not exist")
                val currentType = ApiKeyType.valueOf(row[ApiKeysTable.type])
                if (requiredType != null &&
                    currentType != requiredType
                ) {
                    throw ApiAccessConflict("API key type has changed")
                }
                currentType
            }
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        val token = RawApiToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes))
        val stored =
            StoredApiCredential("{bcrypt}" + BCrypt.withDefaults().hashToString(bcryptCost, token.value.toCharArray()))
        return PreparedApiKeyRotation(id, type, token, stored)
    }

    /** Participates after the caller's current authority check; competing rotations serialize on the key. */
    fun replaceIn(
        transaction: JdbcTransaction,
        prepared: PreparedApiKeyRotation,
    ): RotatedApiKey {
        database.requireTransaction(transaction)
        val row =
            ApiKeysTable
                .selectAll()
                .where { ApiKeysTable.id eq prepared.id.value }
                .forUpdate()
                .firstOrNull()
                ?: throw ApiAccessNotFound("API key does not exist")
        if (row[ApiKeysTable.type] != prepared.type.name) throw ApiAccessConflict("API key type has changed")
        val currentVersion = row[ApiKeysTable.version] ?: 0
        if (prepared.attemptedVersion == null) prepared.attemptedVersion = currentVersion
        // A retry after lost commit acknowledgement must not count the same credential rotation twice.
        if (row[ApiKeysTable.token] != prepared.stored.value) {
            if (currentVersion != prepared.attemptedVersion) {
                throw ApiAccessConflict("API key has changed during rotation")
            }
            val changed =
                ApiKeysTable.update({ ApiKeysTable.id eq prepared.id.value }) {
                    it[token] = prepared.stored.value
                    it[updatedAt] = LocalDateTime.now(ZoneOffset.UTC)
                    it[version] = currentVersion + 1
                }
            if (changed != 1) throw ApiAccessNotFound("API key does not exist")
        }
        val key =
            apiKeysWithDescription()
                .selectAll()
                .where { ApiKeysTable.id eq prepared.id.value }
                .first()
                .toApiKey()
        return RotatedApiKey(key, prepared.token)
    }

    @Suppress("TooGenericExceptionCaught") // Cache availability does not change the committed database credential.
    fun publishAfterCommit(prepared: PreparedApiKeyRotation) {
        check(TransactionManager.currentOrNull() == null) { "API credential caching requires no active transaction" }
        try {
            verificationCache.remember(prepared.id, prepared.stored, prepared.token)
        } catch (failure: CancellationException) {
            throw failure
        } catch (failure: InterruptedException) {
            throw failure
        } catch (_: Exception) {
            // Entries are bound to the exact stored hash; late publication cannot revive an older credential.
        }
    }
}

class PreparedApiKeyRotation internal constructor(
    val id: ApiKeyId,
    internal val type: ApiKeyType,
    val token: RawApiToken,
    internal val stored: StoredApiCredential,
) {
    // Capture the first locked version so a retry cannot overwrite a later successful rotation.
    internal var attemptedVersion: Int? = null

    override fun toString(): String = "PreparedApiKeyRotation(<redacted>)"
}

data class RotatedApiKey(
    val apiKey: ApiKey,
    val token: RawApiToken,
)
