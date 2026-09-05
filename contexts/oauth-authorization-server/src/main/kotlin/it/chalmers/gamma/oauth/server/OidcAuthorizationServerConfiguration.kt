package it.chalmers.gamma.oauth.server

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import it.chalmers.gamma.oauth.AuthorizedOAuthClaims
import it.chalmers.gamma.oauth.OAuthClaimDecisions
import it.chalmers.gamma.platform.core.UserId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import java.time.Instant

@Configuration(proxyBeanMethods = false)
data class OAuthIssuer(
    val publicBaseUrl: String,
)

internal class OidcAuthorizationServerConfiguration {
    @Bean
    fun signingKey(
        @Value("\${OAUTH_SIGNING_KEY_PEM:}") signingKeyPem: String,
    ): RSAKey {
        val key =
            if (signingKeyPem.isBlank()) {
                logger.warn("OAUTH_SIGNING_KEY_PEM is not configured; OAuth tokens will use an ephemeral signing key")
                OAuthSigningKeys.ephemeral().current
            } else {
                OAuthSigningKeys.fromPkcs8Pem(signingKeyPem).current
            }
        return RSAKey
            .Builder(key.publicKey)
            .privateKey(key.privateKey)
            .keyID(key.keyId)
            .build()
    }

    @Bean
    fun jwkSource(signingKey: RSAKey): JWKSource<SecurityContext> {
        val set = JWKSet(signingKey)
        return JWKSource { selector, _ -> selector.select(set) }
    }

    @Bean
    fun jwtDecoder(signingKey: RSAKey): JwtDecoder = NimbusJwtDecoder.withPublicKey(signingKey.toRSAPublicKey()).build()

    @Bean
    fun authorizationServerSettings(issuer: OAuthIssuer): AuthorizationServerSettings =
        AuthorizationServerSettings
            .builder()
            .issuer(issuer.publicBaseUrl)
            .oidcUserInfoEndpoint("/oauth2/userinfo")
            .build()

    @Bean
    fun idTokenClaims(
        claimDecisions: OAuthClaimDecisions,
        issuer: OAuthIssuer,
    ): OAuth2TokenCustomizer<JwtEncodingContext> =
        OAuth2TokenCustomizer { context ->
            if (context.tokenType.value != OidcParameterNames.ID_TOKEN) return@OAuth2TokenCustomizer
            val authorization = context.authorization ?: return@OAuth2TokenCustomizer
            val authorizedClaims =
                claimDecisions.claims(
                    UserId.parse(authorization.principalName),
                    context.authorizedScopes,
                ) ?: return@OAuth2TokenCustomizer
            val claimValues =
                authorizedClaims.toOidcClaimValues(issuer.publicBaseUrl)
            context.claims.claim("sub", claimValues.getValue("sub"))
            val authorizationRequest =
                authorization.getAttribute<OAuth2AuthorizationRequest>(OAuth2AuthorizationRequest::class.java.name)
            if (authorizationRequest?.additionalParameters?.containsKey("max_age") == true) {
                val authenticationTime =
                    checkNotNull(authorization.getAttribute<Instant>(OAUTH_AUTHENTICATION_TIME_ATTRIBUTE)) {
                        "OIDC max_age authorization is missing its validated authentication time"
                    }
                context.claims.claim("auth_time", authenticationTime.epochSecond)
            }
            claimValues
                .filterKeys { it != "sub" }
                .forEach(context.claims::claim)
        }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(OidcAuthorizationServerConfiguration::class.java)
    }
}

internal fun AuthorizedOAuthClaims.toOidcClaimValues(publicBaseUrl: String): Map<String, Any> =
    buildMap {
        put("sub", subject)
        profile?.let { profile ->
            put("cid", profile.cid)
            put("given_name", profile.givenName)
            put("family_name", profile.familyName)
            put("nickname", profile.nickname)
            put("name", profile.displayName)
            put("locale", profile.locale)
            put("picture", "${publicBaseUrl.trimEnd('/')}/images/user/avatar/${profile.pictureUserId.value}")
        }
        email?.let { put("email", it) }
    }
