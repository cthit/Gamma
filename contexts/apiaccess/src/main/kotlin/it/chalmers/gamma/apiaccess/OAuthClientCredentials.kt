package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.LocalizedText
import java.util.UUID

@JvmInline
value class ClientCredentialId(
    val value: UUID,
)

data class IssuedClientCredential(
    val id: ClientCredentialId,
    val token: RawApiToken,
)

data class ClientCredentialSummary(
    val id: ClientCredentialId,
    val name: ApiKeyName,
    val description: LocalizedText,
)

class OAuthClientCredentials(
    private val apiKeys: ApiKeyStore,
) {
    fun issue(
        name: ApiKeyName,
        description: LocalizedText,
    ): IssuedClientCredential {
        val created = apiKeys.createApiKey(name, description, ApiKeyType.CLIENT)
        return IssuedClientCredential(ClientCredentialId(created.apiKey.id.value), created.token)
    }

    fun rotate(id: ClientCredentialId): RawApiToken = apiKeys.resetToken(ApiKeyId(id.value))

    fun find(id: ClientCredentialId): ClientCredentialSummary? =
        apiKeys.findApiKey(ApiKeyId(id.value))?.takeIf { it.type == ApiKeyType.CLIENT }?.let {
            ClientCredentialSummary(id, it.name, it.description)
        }

    fun revokeIfPresent(id: ClientCredentialId) {
        try {
            apiKeys.deleteApiKey(ApiKeyId(id.value))
        } catch (_: ApiAccessNotFound) {
            // A prior cleanup may have committed before its caller observed a failure.
            // Treating absence as completed revocation keeps client retirement retryable.
        }
    }
}
