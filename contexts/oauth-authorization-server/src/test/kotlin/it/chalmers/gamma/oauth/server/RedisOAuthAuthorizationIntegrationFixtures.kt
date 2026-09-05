package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientUid
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2DeviceCode
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.OAuth2UserCode
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import java.net.URI
import java.security.Principal
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID

internal fun registeredClient(
    clientId: String = "regression-client",
    redirectUri: String = "https://client.example/callback",
): RegisteredClient =
    RegisteredClient
        .withId(UUID.randomUUID().toString())
        .clientId(clientId)
        .clientSecret("{noop}secret-$clientId")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .redirectUri(redirectUri)
        .scope("openid")
        .scope("profile")
        .build()

internal fun authorization(
    client: RegisteredClient,
    now: Instant,
    options: AuthorizationOptions = AuthorizationOptions(),
): OAuth2Authorization {
    val principal =
        UsernamePasswordAuthenticationToken.authenticated(
            PRINCIPAL,
            "",
            listOf(SimpleGrantedAuthority("ROLE_USER")),
        )
    val request =
        OAuth2AuthorizationRequest
            .authorizationCode()
            .authorizationUri("https://gamma.example/oauth2/authorize")
            .clientId(client.clientId)
            .redirectUri(options.redirectUri ?: client.redirectUris.single())
            .scopes(setOf("openid", "profile"))
            .state("external-state")
            .additionalParameters(
                mapOf(
                    "code_challenge" to "challenge-value",
                    "code_challenge_method" to "S256",
                    "nonce" to "oidc-nonce",
                ),
            ).build()
    val authorizationCode = OAuth2AuthorizationCode(options.code, now, now.plusSeconds(300))
    val builder =
        OAuth2Authorization
            .withRegisteredClient(client)
            .id(options.id)
            .principalName(PRINCIPAL)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizedScopes(setOf("openid", "profile"))
            .attribute(Principal::class.java.name, principal)
            .attribute(OAuth2AuthorizationRequest::class.java.name, request)
            .attribute("state", "internal-state")
            .token(authorizationCode) { metadata ->
                if (options.invalidatedCode) {
                    metadata[OAuth2Authorization.Token.INVALIDATED_METADATA_NAME] = true
                }
                metadata[OAuth2Authorization.Token.CLAIMS_METADATA_NAME] = mapOf("code" to "claim")
            }
    if (options.onlyCode) return builder.build()

    val accessToken =
        OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "access-token",
            now,
            now.plusSeconds(600),
            setOf("openid", "profile"),
        )
    builder.token(accessToken) { metadata ->
        metadata[OAuth2Authorization.Token.CLAIMS_METADATA_NAME] =
            mapOf("sub" to "subject", "issued" to now, "aud" to setOf("regression-client"))
    }
    builder.token(OAuth2RefreshToken("refresh-token", now, now.plusSeconds(1_200)))
    builder.token(
        OidcIdToken(
            "id-token",
            now,
            now.plusSeconds(600),
            mapOf("sub" to "subject", "iss" to URI("https://gamma.example").toURL()),
        ),
    )
    builder.token(OAuth2DeviceCode("device-code", now, now.plusSeconds(300)))
    builder.token(OAuth2UserCode("user-code", now, now.plusSeconds(300)))
    return builder.build()
}

internal data class AuthorizationOptions(
    val id: String = UUID.randomUUID().toString(),
    val code: String = "authorization-code",
    val onlyCode: Boolean = false,
    val redirectUri: String? = null,
    val invalidatedCode: Boolean = true,
)

internal fun lookups(authorization: OAuth2Authorization): List<Pair<String, OAuth2TokenType>> =
    buildList {
        authorization.getAttribute<String>("state")?.let { add(it to OAuth2TokenType("state")) }
        authorization
            .getAttribute<OAuth2AuthorizationRequest>(OAuth2AuthorizationRequest::class.java.name)
            ?.state
            ?.let { add(it to OAuth2TokenType("state")) }
        authorization.getToken(OAuth2AuthorizationCode::class.java)?.token?.tokenValue?.let {
            add(it to OAuth2TokenType("code"))
        }
        authorization.accessToken
            ?.token
            ?.tokenValue
            ?.let { add(it to OAuth2TokenType.ACCESS_TOKEN) }
        authorization.refreshToken
            ?.token
            ?.tokenValue
            ?.let { add(it to OAuth2TokenType.REFRESH_TOKEN) }
        authorization.getToken(OidcIdToken::class.java)?.token?.tokenValue?.let {
            add(it to OAuth2TokenType("id_token"))
        }
        authorization.getToken(OAuth2DeviceCode::class.java)?.token?.tokenValue?.let {
            add(it to OAuth2TokenType("device_code"))
        }
        authorization.getToken(OAuth2UserCode::class.java)?.token?.tokenValue?.let {
            add(it to OAuth2TokenType("user_code"))
        }
    }

internal class MutableClock(
    private var current: Instant,
) : Clock() {
    override fun getZone(): ZoneId = ZoneOffset.UTC

    override fun withZone(zone: ZoneId): Clock {
        require(zone == ZoneOffset.UTC)
        return this
    }

    override fun instant(): Instant = current

    fun advance(duration: Duration) {
        current = current.plus(duration)
    }
}

internal const val PRINCIPAL = "88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f"

internal fun redisOAuth2AuthorizationService(
    store: RedisOAuthAuthorizationStore,
    clients: RegisteredClientRepository,
    clock: Clock,
    currentAuthenticationTime: () -> Instant? = { null },
    currentClientId: () -> String? = { null },
): RedisOAuth2AuthorizationService =
    RedisOAuth2AuthorizationService(
        store = store,
        clients = clients,
        currentAuthenticationTime = currentAuthenticationTime,
        currentClientId = currentClientId,
        clock = clock,
    )
