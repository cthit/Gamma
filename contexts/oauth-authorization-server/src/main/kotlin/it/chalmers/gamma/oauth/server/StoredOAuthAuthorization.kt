package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.platform.redis.GammaRedis
import kotlinx.serialization.Serializable
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.UUID

internal enum class OAuthAuthorizationIndexKind(
    val storageName: String,
) {
    STATE("state"),
    AUTHORIZATION_CODE("code"),
    ACCESS_TOKEN("access"),
    REFRESH_TOKEN("refresh"),
    ID_TOKEN("id"),
    DEVICE_CODE("device"),
    USER_CODE("user"),
}

@Serializable
internal data class StoredOAuthAuthorizationIndex(
    val kind: OAuthAuthorizationIndexKind,
    val digest: String,
)

@Serializable
internal data class StoredOAuthAuthorization(
    val version: Int = 1,
    val revision: String,
    val id: String,
    val registeredClientId: String,
    val principalName: String,
    val authorizationGrantType: String,
    val authorizedScopes: Set<String>,
    val principalAuthorities: Set<String>,
    val authorizationRequest: StoredOAuthAuthorizationRequest?,
    val state: String?,
    val tokens: List<StoredOAuthToken>,
    val indexes: Set<StoredOAuthAuthorizationIndex>,
    val expiresAtEpochMilli: Long,
    val authenticationTimeEpochSecond: Long? = null,
) {
    val expiresAt: Instant
        get() = Instant.ofEpochMilli(expiresAtEpochMilli)

    fun validate(clock: Clock) {
        require(version == 1) { "Unknown OAuth authorization payload version" }
        require(runCatching { UUID.fromString(revision) }.isSuccess) { "Invalid OAuth authorization revision" }
        require(id.isNotBlank() && id.length <= 256) { "Invalid OAuth authorization id" }
        require(registeredClientId.isNotBlank() && registeredClientId.length <= 256) {
            "Invalid OAuth registered client id"
        }
        require(principalName.isNotBlank() && principalName.length <= 256) { "Invalid OAuth principal" }
        require(authorizationGrantType.isNotBlank() && authorizationGrantType.length <= 128) {
            "Invalid OAuth authorization grant type"
        }
        require(authorizedScopes.size <= 128 && authorizedScopes.all { it.length in 1..256 }) {
            "Invalid OAuth authorized scopes"
        }
        require(principalAuthorities.size <= 128 && principalAuthorities.all { it.length in 1..256 }) {
            "Invalid OAuth principal authorities"
        }
        require(
            authenticationTimeEpochSecond == null ||
                authenticationTimeEpochSecond in 0..clock.instant().plusSeconds(5).epochSecond,
        ) {
            "Invalid OAuth authentication time"
        }
        require(tokens.size <= 8) { "Too many OAuth tokens" }
        require(indexes.size <= 8 && indexes.all { it.digest.length == 43 }) { "Invalid OAuth lookup indexes" }
        require(tokens.map(StoredOAuthToken::kind).distinct().size == tokens.size) { "Duplicate OAuth token kind" }
        tokens.forEach(StoredOAuthToken::validate)
        authorizationRequest?.validate()
        val expectedIndexes =
            buildSet {
                val clientId = authorizationRequest?.clientId
                state?.let {
                    add(
                        StoredOAuthAuthorizationIndex(
                            OAuthAuthorizationIndexKind.STATE,
                            GammaRedis.digest("${requireNotNull(clientId)}\u0000$it"),
                        ),
                    )
                }
                authorizationRequest?.state?.let {
                    add(
                        StoredOAuthAuthorizationIndex(
                            OAuthAuthorizationIndexKind.STATE,
                            GammaRedis.digest("${authorizationRequest.clientId}\u0000$it"),
                        ),
                    )
                }
                tokens.forEach { token ->
                    add(StoredOAuthAuthorizationIndex(token.kind.indexKind, GammaRedis.digest(token.tokenValue)))
                }
            }
        require(indexes == expectedIndexes) { "OAuth lookup indexes are inconsistent" }
        require(tokens.mapNotNull(StoredOAuthToken::expiresAt).all { !it.isAfter(expiresAt) }) {
            "OAuth authorization expires before one of its tokens"
        }
        require(!expiresAt.isAfter(clock.instant().plus(Duration.ofDays(31)))) {
            "OAuth authorization expiry exceeds the supported lifetime"
        }
    }

    override fun toString(): String =
        "StoredOAuthAuthorization(version=$version, id=<redacted>, registeredClientId=<redacted>, " +
            "principalName=<redacted>, tokens=<redacted>, indexes=<redacted>, expiresAt=<redacted>)"
}

@Serializable
internal data class StoredOAuthAuthorizationRequest(
    val authorizationUri: String,
    val clientId: String,
    val redirectUri: String,
    val scopes: Set<String>,
    val state: String?,
    val additionalParameters: Map<String, StoredOAuthValue>,
    val attributes: Map<String, StoredOAuthValue>,
    val authorizationRequestUri: String?,
) {
    fun validate() {
        require(authorizationUri.length in 1..MAXIMUM_VALUE_STRING_LENGTH) { "Invalid OAuth authorization URI" }
        require(clientId.length in 1..256) { "Invalid OAuth request client" }
        require(redirectUri.length in 1..MAXIMUM_VALUE_STRING_LENGTH) { "Invalid OAuth redirect URI" }
        require(scopes.size <= 128 && scopes.all { it.length in 1..256 }) { "Invalid OAuth request scopes" }
        require(state == null || state.length <= MAXIMUM_SENSITIVE_VALUE_LENGTH) { "Invalid OAuth state" }
        additionalParameters.values.forEach(StoredOAuthValue::validate)
        attributes.values.forEach(StoredOAuthValue::validate)
    }

    override fun toString(): String = "StoredOAuthAuthorizationRequest(<redacted>)"
}

@Serializable
internal enum class StoredOAuthTokenKind(
    val indexKind: OAuthAuthorizationIndexKind,
) {
    AUTHORIZATION_CODE(OAuthAuthorizationIndexKind.AUTHORIZATION_CODE),
    ACCESS_TOKEN(OAuthAuthorizationIndexKind.ACCESS_TOKEN),
    REFRESH_TOKEN(OAuthAuthorizationIndexKind.REFRESH_TOKEN),
    ID_TOKEN(OAuthAuthorizationIndexKind.ID_TOKEN),
    DEVICE_CODE(OAuthAuthorizationIndexKind.DEVICE_CODE),
    USER_CODE(OAuthAuthorizationIndexKind.USER_CODE),
}

@Serializable
internal data class StoredOAuthToken(
    val kind: StoredOAuthTokenKind,
    val tokenValue: String,
    val issuedAtEpochMilli: Long?,
    val expiresAtEpochMilli: Long?,
    val accessTokenType: String?,
    val scopes: Set<String>,
    val claims: Map<String, StoredOAuthValue>,
    val metadata: Map<String, StoredOAuthValue>,
) {
    val issuedAt: Instant?
        get() = issuedAtEpochMilli?.let(Instant::ofEpochMilli)
    val expiresAt: Instant?
        get() = expiresAtEpochMilli?.let(Instant::ofEpochMilli)

    fun requireExpiry(): Instant = expiresAt ?: throw OAuthAuthorizationStorageFailure("OAuth token expiry is invalid")

    fun validate() {
        require(tokenValue.length in 1..MAXIMUM_SENSITIVE_VALUE_LENGTH) { "Invalid OAuth token value" }
        val validIssuedAt = requireNotNull(issuedAt) { "Invalid OAuth token issue time" }
        val validExpiry = expiresAt
        require(validExpiry == null || validExpiry.isAfter(validIssuedAt)) { "Invalid OAuth token expiry" }
        require(scopes.size <= 128 && scopes.all { it.length in 1..256 }) { "Invalid OAuth token scopes" }
        require(kind == StoredOAuthTokenKind.ACCESS_TOKEN || accessTokenType == null) {
            "Unexpected OAuth access token type"
        }
        require(kind == StoredOAuthTokenKind.ACCESS_TOKEN || scopes.isEmpty()) { "Unexpected OAuth token scopes" }
        require(claims.size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth token claims are too large" }
        require(metadata.size <= MAXIMUM_VALUE_COLLECTION_SIZE) { "OAuth token metadata is too large" }
        claims.values.forEach(StoredOAuthValue::validate)
        metadata.values.forEach(StoredOAuthValue::validate)
    }

    override fun toString(): String = "StoredOAuthToken(kind=$kind, value=<redacted>, metadata=<redacted>)"
}
