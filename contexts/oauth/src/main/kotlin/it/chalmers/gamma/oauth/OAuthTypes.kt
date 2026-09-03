package it.chalmers.gamma.oauth

import it.chalmers.gamma.platform.core.LocalizedText
import it.chalmers.gamma.platform.core.UserId
import java.util.UUID

typealias ClientUid = it.chalmers.gamma.platform.core.ClientUid

@JvmInline
value class ClientId(
    val value: String,
) {
    init {
        require(value.matches(Regex("^[A-Z0-9]{30}$"))) {
            "Client id must contain exactly 30 uppercase letters or numbers"
        }
    }
}

@JvmInline
value class RawClientSecret(
    val value: String,
) {
    init {
        require(value.length in 32..100) { "Client secret must contain between 32 and 100 characters" }
    }

    override fun toString(): String = "<value redacted>"
}

@JvmInline
value class RedirectUri(
    val value: String,
) {
    init {
        require(value.isNotEmpty() && value.none { it in setOf('&', '<', '>', '"', '\'') }) {
            "Redirect URI must be non-empty and must not contain HTML markup characters"
        }
    }
}

@JvmInline
value class ClientName(
    val value: String,
) {
    init {
        require(value.length in 2..30)
        require(value.none { it in setOf('&', '<', '>', '"', '\'') })
    }
}

@JvmInline
value class AuthorityName(
    val value: String,
) {
    init {
        require(value.matches(Regex("^[0-9a-z]{2,30}$"))) {
            "Authority names must contain 2 to 30 lowercase letters or numbers"
        }
    }
}

@JvmInline
value class OAuthApiKeyId(
    val value: UUID,
) {
    companion object {
        fun generate() = OAuthApiKeyId(UUID.randomUUID())

        fun parse(value: String) = OAuthApiKeyId(UUID.fromString(value))
    }
}

@JvmInline
value class OAuthApiToken(
    val value: String,
) {
    init {
        require(value.length in 32..100)
    }

    override fun toString(): String = "<value redacted>"
}

enum class Scope(
    val wireValue: String,
) {
    OPENID("openid"),
    PROFILE("profile"),
    EMAIL("email"),
}

sealed interface ClientOwner {
    data object Official : ClientOwner

    data class User(
        val userId: UserId,
    ) : ClientOwner
}

data class OAuthClient(
    val uid: ClientUid,
    val clientId: ClientId,
    val redirectUri: RedirectUri,
    val name: ClientName,
    val description: LocalizedText,
    val scopes: Set<Scope>,
    val owner: ClientOwner,
    val apiKeyId: OAuthApiKeyId? = null,
    val restrictedSuperGroupIds: Set<UUID> = emptySet(),
)

data class NewOAuthClient(
    val redirectUri: RedirectUri,
    val name: ClientName,
    val description: LocalizedText,
    val includeEmailScope: Boolean,
    val owner: ClientOwner,
    val generateApiKey: Boolean = false,
    val restrictedSuperGroupIds: Set<UUID> = emptySet(),
)

data class CreatedOAuthClient(
    val client: OAuthClient,
    val secret: RawClientSecret,
    val apiCredential: ClientApiCredential? = null,
)

data class ClientApiCredential(
    val id: OAuthApiKeyId,
    val token: OAuthApiToken,
)

data class ClientAuthority(
    val clientUid: ClientUid,
    val name: AuthorityName,
    val userIds: Set<UserId>,
    val superGroupIds: Set<UUID>,
)
