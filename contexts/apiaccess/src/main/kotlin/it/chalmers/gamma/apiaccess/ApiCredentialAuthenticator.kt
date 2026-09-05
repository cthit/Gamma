package it.chalmers.gamma.apiaccess

import at.favre.lib.crypto.bcrypt.BCrypt
import it.chalmers.gamma.platform.database.DatabaseFactory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.concurrent.CancellationException

class ApiCredentialAuthenticator(
    private val database: DatabaseFactory,
    private val verificationCache: ApiTokenVerificationCache = ApiTokenVerificationCache.Disabled,
) {
    // Keep credential capture, external verification, and final acceptance in one readable flow.
    @Suppress("TooGenericExceptionCaught")
    fun authenticate(
        id: ApiKeyId,
        token: RawApiToken,
        requiredType: ApiKeyType? = null,
    ): ApiKey? {
        val credential =
            database.commitTransaction(readOnly = true) {
                val row =
                    ApiKeysTable
                        .select(ApiKeysTable.token, ApiKeysTable.type)
                        .where { ApiKeysTable.id eq id.value }
                        .limit(1)
                        .firstOrNull() ?: return@commitTransaction null
                val type = row[ApiKeysTable.type]
                if (requiredType != null && type != requiredType.name) return@commitTransaction null
                ApiCredentialSnapshot(StoredApiCredential(row[ApiKeysTable.token]), type)
            } ?: return null

        val cached =
            try {
                verificationCache.match(id, credential.stored, token)
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: InterruptedException) {
                throw failure
            } catch (_: Exception) {
                CachedApiTokenMatch.MISS
            }
        if (cached == CachedApiTokenMatch.MISMATCH) return null
        if (cached == CachedApiTokenMatch.MISS) {
            val matches =
                BCrypt
                    .verifyer()
                    .verify(
                        token.value.toCharArray(),
                        credential.stored.value
                            .removePrefix("{bcrypt}")
                            .toCharArray(),
                    ).verified
            if (!matches) return null
            try {
                // Cache entries are bound to this exact stored hash. Acceptance still requires
                // the final database read, including a rotation occurring during this cache write.
                verificationCache.remember(id, credential.stored, token)
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: InterruptedException) {
                throw failure
            } catch (_: Exception) {
                // An ordinary cache outage does not change the database's authority.
            }
        }

        return database.commitTransaction(readOnly = true) {
            val row =
                apiKeysWithDescription()
                    .selectAll()
                    .where { ApiKeysTable.id eq id.value }
                    .limit(1)
                    .firstOrNull() ?: return@commitTransaction null
            if (row[ApiKeysTable.token] != credential.stored.value || row[ApiKeysTable.type] != credential.type) {
                return@commitTransaction null
            }
            row.toApiKey()
        }
    }
}

private data class ApiCredentialSnapshot(
    val stored: StoredApiCredential,
    val type: String,
) {
    override fun toString(): String = "ApiCredentialSnapshot(<redacted>)"
}
