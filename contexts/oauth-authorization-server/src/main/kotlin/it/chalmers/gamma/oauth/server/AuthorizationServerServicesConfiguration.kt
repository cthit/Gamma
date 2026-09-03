package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.oauth.OAuthProtocolConsents
import it.chalmers.gamma.users.UserStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.authority.FactorGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Configuration(proxyBeanMethods = false)
internal class AuthorizationServerServicesConfiguration {
    @Bean
    fun registeredClientRepository(clients: OAuthProtocolClients): RegisteredClientRepository =
        GammaRegisteredClientRepository(clients)

    @Bean
    fun authorizationService(
        authorizationStore: RedisOAuthAuthorizationStore,
        clients: RegisteredClientRepository,
    ): OAuth2AuthorizationService =
        RedisOAuth2AuthorizationService(
            store = authorizationStore,
            clients = clients,
            currentAuthenticationTime = {
                SecurityContextHolder
                    .getContext()
                    .authentication
                    ?.authorities
                    ?.filterIsInstance<FactorGrantedAuthority>()
                    ?.firstOrNull { it.authority == FactorGrantedAuthority.PASSWORD_AUTHORITY }
                    ?.issuedAt
            },
            currentClientId = {
                (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)
                    ?.request
                    ?.getParameter("client_id")
            },
        )

    @Bean
    fun authorizationConsentService(
        consents: OAuthProtocolConsents,
        users: UserStore,
        clients: RegisteredClientRepository,
    ): OAuth2AuthorizationConsentService = ApprovalOAuth2AuthorizationConsentService(consents, users, clients)
}
