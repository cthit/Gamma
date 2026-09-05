package it.chalmers.gamma.apiaccess

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.SuperGroupType

typealias ApiKeyId = it.chalmers.gamma.platform.core.ApiKeyId

@JvmInline
value class ApiKeyName(
    val value: String,
) {
    init {
        require(value.length in 2..30) { "API key name must be between 2 and 30 characters" }
        require(value.none { it in setOf('&', '<', '>', '"', '\'') }) { "API key name contains unsafe text" }
    }
}

enum class ApiKeyType {
    CLIENT,
    ACCOUNT_SCAFFOLD,
    INFO,
    ALLOW_LIST,
}

@JvmInline
value class RawApiToken(
    val value: String,
) {
    init {
        require(value.length in 32..100) { "API token must contain between 32 and 100 characters" }
    }

    override fun toString(): String = "<value redacted>"
}

data class ApiKey(
    val id: ApiKeyId,
    val name: ApiKeyName,
    val description: LocalizedText,
    val type: ApiKeyType,
    val version: Int,
)

sealed interface ApiKeySettingsUpdate {
    val version: Int
}

data class ApiKeyInfoSettings(
    override val version: Int,
    val superGroupTypes: List<SuperGroupType>,
) : ApiKeySettingsUpdate

data class ApiKeyAccountScaffoldSettings(
    override val version: Int,
    val superGroupTypes: List<SuperGroupTypeSetting>,
) : ApiKeySettingsUpdate

data class SuperGroupTypeSetting(
    val type: SuperGroupType,
    val requiresManaged: Boolean,
)

data class CreatedApiKey(
    val apiKey: ApiKey,
    val token: RawApiToken,
)

@JvmInline
value class StoredApiCredential(
    val value: String,
) {
    override fun toString(): String = "<value redacted>"
}
