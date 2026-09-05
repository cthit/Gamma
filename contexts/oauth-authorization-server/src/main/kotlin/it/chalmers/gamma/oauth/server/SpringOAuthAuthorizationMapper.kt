package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.platform.redis.GammaRedis
import it.chalmers.gamma.users.GammaPrincipal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.FactorGrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2DeviceCode
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.OAuth2Token
import org.springframework.security.oauth2.core.OAuth2UserCode
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.web.util.UriComponentsBuilder
import java.security.Principal
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.UUID

internal fun OAuth2Authorization.toStored(
    registeredClient: RegisteredClient,
    clock: Clock,
): StoredOAuthAuthorization {
    require(registeredClient.id == registeredClientId) { "OAuth registered client is inconsistent" }
    require(authorizationGrantType in registeredClient.authorizationGrantTypes) {
        "OAuth authorization grant is no longer allowed"
    }
    require(registeredClient.scopes.containsAll(authorizedScopes)) {
        "OAuth authorization contains a scope that is no longer allowed"
    }

    val principal = getAttribute<Authentication>(Principal::class.java.name)
    require(principal == null || principal.name == principalName) { "OAuth principal is inconsistent" }
    val request =
        getAttribute<OAuth2AuthorizationRequest>(OAuth2AuthorizationRequest::class.java.name)
            ?.toStored(registeredClient)
    val stateAttribute = getAttribute<Any>(STATE_ATTRIBUTE)
    require(stateAttribute == null || stateAttribute is String)
    val state = stateAttribute
    val supportedAttributeNames =
        setOf(
            Principal::class.java.name,
            OAuth2AuthorizationRequest::class.java.name,
            STATE_ATTRIBUTE,
            STORAGE_REVISION_ATTRIBUTE,
            OAUTH_AUTHENTICATION_TIME_ATTRIBUTE,
        )
    require(attributes.keys.all { it in supportedAttributeNames }) {
        "OAuth authorization contains an unsupported attribute"
    }

    val tokens =
        buildList {
            getToken(OAuth2AuthorizationCode::class.java)?.let {
                add(it.toStored(StoredOAuthTokenKind.AUTHORIZATION_CODE))
            }
            getToken(OAuth2AccessToken::class.java)?.let { add(it.toStored(StoredOAuthTokenKind.ACCESS_TOKEN)) }
            getToken(OAuth2RefreshToken::class.java)?.let { add(it.toStored(StoredOAuthTokenKind.REFRESH_TOKEN)) }
            getToken(OidcIdToken::class.java)?.let { add(it.toStored(StoredOAuthTokenKind.ID_TOKEN)) }
            getToken(OAuth2DeviceCode::class.java)?.let { add(it.toStored(StoredOAuthTokenKind.DEVICE_CODE)) }
            getToken(OAuth2UserCode::class.java)?.let { add(it.toStored(StoredOAuthTokenKind.USER_CODE)) }
        }
    val indexes =
        buildSet {
            state?.let { value -> add(stateIndex(requireNotNull(request).clientId, value)) }
            request?.state?.let { value -> add(stateIndex(request.clientId, value)) }
            tokens.forEach { storedToken -> add(index(storedToken.kind.indexKind, storedToken.tokenValue)) }
        }
    val now = clock.instant()
    val longestTokenExpiry =
        tokens.maxOfOrNull { token ->
            token.expiresAt ?: token.issuedAt?.plus(MAXIMUM_AUTHORIZATION_LIFETIME) ?: now.plus(FLOW_LIFETIME)
        }
    val expiresAt =
        maxOf(
            longestTokenExpiry ?: now.plus(FLOW_LIFETIME),
            // Spring deliberately accepts expired ID-token hints for RP-initiated logout.
            // Keep the 2.5.1 one-hour authorization lookup window for that compatibility.
            now.plus(AUTHORIZATION_LOOKUP_LIFETIME),
        ).coerceAtMost(now.plus(MAXIMUM_AUTHORIZATION_LIFETIME))
    require(expiresAt.isAfter(now)) { "OAuth authorization has expired" }

    return StoredOAuthAuthorization(
        revision = UUID.randomUUID().toString(),
        id = id,
        registeredClientId = registeredClientId,
        principalName = principalName,
        authorizationGrantType = authorizationGrantType.value,
        authorizedScopes = authorizedScopes.toSortedSet(),
        principalAuthorities =
            principal
                ?.authorities
                .orEmpty()
                .mapNotNull { it.authority }
                .toSortedSet(),
        authenticationTimeEpochSecond =
            (
                getAttribute<Instant>(OAUTH_AUTHENTICATION_TIME_ATTRIBUTE)
                    ?: principal
                        ?.authorities
                        .orEmpty()
                        .filterIsInstance<FactorGrantedAuthority>()
                        .maxOfOrNull(FactorGrantedAuthority::getIssuedAt)
            )?.epochSecond,
        authorizationRequest = request,
        state = state,
        tokens = tokens,
        indexes = indexes,
        expiresAtEpochMilli = expiresAt.toEpochMilli(),
    )
}

private fun OAuth2AuthorizationRequest.toStored(registeredClient: RegisteredClient): StoredOAuthAuthorizationRequest {
    require(grantType == AuthorizationGrantType.AUTHORIZATION_CODE) {
        "OAuth authorization request grant is unsupported"
    }
    require(clientId == registeredClient.clientId) { "OAuth authorization request client is inconsistent" }
    require(registeredClient.allowsRedirectUri(redirectUri)) {
        "OAuth authorization request redirect URI is no longer allowed"
    }
    require(registeredClient.scopes.containsAll(scopes)) {
        "OAuth authorization request contains a scope that is no longer allowed"
    }
    return StoredOAuthAuthorizationRequest(
        authorizationUri = authorizationUri,
        clientId = clientId,
        redirectUri = requireNotNull(redirectUri),
        scopes = scopes.toSortedSet(),
        state = state,
        additionalParameters = additionalParameters.toStoredValues(),
        attributes = attributes.toStoredValues(),
        authorizationRequestUri = authorizationRequestUri,
    )
}

private fun OAuth2Authorization.Token<out OAuth2Token>.toStored(kind: StoredOAuthTokenKind): StoredOAuthToken {
    val value = token
    require(value.tokenValue.isNotBlank() && value.tokenValue.length <= MAXIMUM_SENSITIVE_VALUE_LENGTH) {
        "OAuth token value is invalid"
    }
    val accessToken = value as? OAuth2AccessToken
    val idToken = value as? OidcIdToken
    return StoredOAuthToken(
        kind = kind,
        tokenValue = value.tokenValue,
        issuedAtEpochMilli = value.issuedAt?.toEpochMilli(),
        expiresAtEpochMilli = value.expiresAt?.toEpochMilli(),
        accessTokenType = accessToken?.tokenType?.value,
        scopes = accessToken?.scopes.orEmpty().toSortedSet(),
        claims = idToken?.claims.orEmpty().toStoredValues(),
        metadata = metadata.toStoredValues(),
    )
}

internal fun StoredOAuthAuthorization.toSpringAuthorization(
    clients: RegisteredClientRepository,
): OAuth2Authorization? {
    val registeredClient = clients.findById(registeredClientId) ?: return null
    val grantType = AuthorizationGrantType(authorizationGrantType)
    if (grantType !in registeredClient.authorizationGrantTypes) return null
    if (!registeredClient.scopes.containsAll(authorizedScopes)) return null

    val builder =
        OAuth2Authorization
            .withRegisteredClient(registeredClient)
            .id(id)
            .principalName(principalName)
            .authorizationGrantType(grantType)
            .authorizedScopes(authorizedScopes)
    val authenticationTime = authenticationTimeEpochSecond?.let(Instant::ofEpochSecond)
    val authorities =
        principalAuthorities.map { authority ->
            if (authority == FactorGrantedAuthority.PASSWORD_AUTHORITY && authenticationTime != null) {
                FactorGrantedAuthority.withAuthority(authority).issuedAt(authenticationTime).build()
            } else {
                SimpleGrantedAuthority(authority)
            }
        }
    val principal =
        UsernamePasswordAuthenticationToken.authenticated(
            GammaPrincipal(
                userId = principalName,
                nick = "",
                administrator = principalAuthorities.contains("ROLE_ADMIN"),
            ),
            "",
            authorities,
        )
    builder.attribute(Principal::class.java.name, principal)
    builder.attribute(STORAGE_REVISION_ATTRIBUTE, revision)
    authenticationTime?.let { instant ->
        builder.attribute(OAUTH_AUTHENTICATION_TIME_ATTRIBUTE, instant)
    }
    authorizationRequest?.toSpringRequest(registeredClient)?.let { request ->
        builder.attribute(OAuth2AuthorizationRequest::class.java.name, request)
    }
    state?.let { builder.attribute(STATE_ATTRIBUTE, it) }
    tokens.forEach { storedToken ->
        val token = storedToken.toSpringToken()
        val metadata = storedToken.metadata.toRuntimeValues()
        builder.token(token) { destination -> destination.putAll(metadata) }
    }
    return builder.build()
}

private fun StoredOAuthAuthorizationRequest.toSpringRequest(
    registeredClient: RegisteredClient,
): OAuth2AuthorizationRequest? {
    if (clientId != registeredClient.clientId) return null
    if (!registeredClient.allowsRedirectUri(redirectUri)) return null
    if (!registeredClient.scopes.containsAll(scopes)) return null
    return OAuth2AuthorizationRequest
        .authorizationCode()
        .authorizationUri(authorizationUri)
        .clientId(clientId)
        .redirectUri(redirectUri)
        .scopes(scopes)
        .state(state)
        .additionalParameters(additionalParameters.toRuntimeValues())
        .attributes(attributes.toRuntimeValues())
        .apply { authorizationRequestUri?.let(::authorizationRequestUri) }
        .build()
}

/**
 * Mirrors Spring Authorization Server's redirect validator. RFC 8252 requires an
 * authorization server to accept any port for a pre-registered loopback IP URI;
 * every other redirect URI remains an exact string match.
 */
private fun RegisteredClient.allowsRedirectUri(requestedRedirectUri: String?): Boolean {
    if (requestedRedirectUri == null) return false
    val requestedRedirect =
        runCatching { UriComponentsBuilder.fromUriString(requestedRedirectUri).build() }
            .getOrNull() ?: return false
    if (requestedRedirect.fragment != null) return false
    if (!requestedRedirect.host.isLoopbackIpAddress()) {
        return requestedRedirectUri in redirectUris
    }
    return redirectUris.any { registeredRedirectUri ->
        runCatching {
            UriComponentsBuilder
                .fromUriString(registeredRedirectUri)
                .port(requestedRedirect.port)
                .build()
                .toString() == requestedRedirect.toString()
        }.getOrDefault(false)
    }
}

private fun String?.isLoopbackIpAddress(): Boolean {
    if (this == null) return false
    if (this == "[0:0:0:0:0:0:0:1]" || this == "[::1]") return true
    val octets = split('.')
    if (octets.size != 4) return false
    val address = octets.map { it.toIntOrNull() ?: return false }
    return address[0] == 127 &&
        address[1] in 0..255 &&
        address[2] in 0..255 &&
        address[3] in 1..255
}

private fun StoredOAuthToken.toSpringToken(): OAuth2Token {
    val issuedAt = issuedAt ?: throw OAuthAuthorizationStorageFailure("OAuth token issue time is invalid")
    return when (kind) {
        StoredOAuthTokenKind.AUTHORIZATION_CODE -> {
            OAuth2AuthorizationCode(tokenValue, issuedAt, requireExpiry())
        }

        StoredOAuthTokenKind.ACCESS_TOKEN -> {
            val type =
                when (accessTokenType) {
                    OAuth2AccessToken.TokenType.BEARER.value -> OAuth2AccessToken.TokenType.BEARER
                    OAuth2AccessToken.TokenType.DPOP.value -> OAuth2AccessToken.TokenType.DPOP
                    else -> throw OAuthAuthorizationStorageFailure("OAuth access token type is invalid")
                }
            OAuth2AccessToken(type, tokenValue, issuedAt, requireExpiry(), scopes)
        }

        StoredOAuthTokenKind.REFRESH_TOKEN -> {
            OAuth2RefreshToken(tokenValue, issuedAt, expiresAt)
        }

        StoredOAuthTokenKind.ID_TOKEN -> {
            OidcIdToken(
                tokenValue,
                issuedAt,
                requireExpiry(),
                claims.toRuntimeValues(),
            )
        }

        StoredOAuthTokenKind.DEVICE_CODE -> {
            OAuth2DeviceCode(tokenValue, issuedAt, requireExpiry())
        }

        StoredOAuthTokenKind.USER_CODE -> {
            OAuth2UserCode(tokenValue, issuedAt, requireExpiry())
        }
    }
}

internal fun OAuth2Authorization.matches(
    kind: OAuthAuthorizationIndexKind,
    sensitiveValue: String,
): Boolean =
    when (kind) {
        OAuthAuthorizationIndexKind.STATE -> {
            getAttribute<String>(STATE_ATTRIBUTE) == sensitiveValue ||
                getAttribute<OAuth2AuthorizationRequest>(OAuth2AuthorizationRequest::class.java.name)?.state ==
                sensitiveValue
        }

        OAuthAuthorizationIndexKind.AUTHORIZATION_CODE -> {
            getToken(OAuth2AuthorizationCode::class.java)?.token?.tokenValue == sensitiveValue
        }

        OAuthAuthorizationIndexKind.ACCESS_TOKEN -> {
            accessToken?.token?.tokenValue == sensitiveValue
        }

        OAuthAuthorizationIndexKind.REFRESH_TOKEN -> {
            refreshToken?.token?.tokenValue == sensitiveValue
        }

        OAuthAuthorizationIndexKind.ID_TOKEN -> {
            getToken(OidcIdToken::class.java)?.token?.tokenValue == sensitiveValue
        }

        OAuthAuthorizationIndexKind.DEVICE_CODE -> {
            getToken(OAuth2DeviceCode::class.java)?.token?.tokenValue == sensitiveValue
        }

        OAuthAuthorizationIndexKind.USER_CODE -> {
            getToken(OAuth2UserCode::class.java)?.token?.tokenValue == sensitiveValue
        }
    }

internal fun OAuth2TokenType.toIndexKind(): OAuthAuthorizationIndexKind? =
    when (value) {
        "state" -> OAuthAuthorizationIndexKind.STATE
        "code" -> OAuthAuthorizationIndexKind.AUTHORIZATION_CODE
        "access_token" -> OAuthAuthorizationIndexKind.ACCESS_TOKEN
        "refresh_token" -> OAuthAuthorizationIndexKind.REFRESH_TOKEN
        "id_token" -> OAuthAuthorizationIndexKind.ID_TOKEN
        "device_code" -> OAuthAuthorizationIndexKind.DEVICE_CODE
        "user_code" -> OAuthAuthorizationIndexKind.USER_CODE
        else -> null
    }

private fun index(
    kind: OAuthAuthorizationIndexKind,
    sensitiveValue: String,
) = StoredOAuthAuthorizationIndex(kind, GammaRedis.digest(sensitiveValue))

private fun stateIndex(
    clientId: String,
    sensitiveValue: String,
) = index(OAuthAuthorizationIndexKind.STATE, "$clientId\u0000$sensitiveValue")

internal const val STATE_ATTRIBUTE = "state"
internal const val STORAGE_REVISION_ATTRIBUTE = "it.chalmers.gamma.oauth.server.storageRevision"
internal const val OAUTH_AUTHENTICATION_TIME_ATTRIBUTE = "it.chalmers.gamma.oauth.server.authenticationTime"
private val FLOW_LIFETIME: Duration = Duration.ofMinutes(10)
private val AUTHORIZATION_LOOKUP_LIFETIME: Duration = Duration.ofHours(1)
private val MAXIMUM_AUTHORIZATION_LIFETIME: Duration = Duration.ofDays(30)
