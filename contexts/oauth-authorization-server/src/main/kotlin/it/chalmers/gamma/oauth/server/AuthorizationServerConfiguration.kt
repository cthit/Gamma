package it.chalmers.gamma.oauth.server

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.AuthorizationGrantType

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@Import(
    AuthorizationServerHttpSecurityConfiguration::class,
    AuthorizationServerServicesConfiguration::class,
    OidcAuthorizationServerConfiguration::class,
    ConsentController::class,
)
class AuthorizationServerConfiguration

internal val SUPPORTED_AUTHORIZATION_GRANT_TYPES = setOf(AuthorizationGrantType.AUTHORIZATION_CODE.value)
