package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientApprovals
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.organization.OrganizationQueries
import it.chalmers.gamma.platform.database.DatabaseFactory
import it.chalmers.gamma.users.UserAccountAccess
import it.chalmers.gamma.users.UserQueries
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
    fun oauthClientAccess(
        database: DatabaseFactory,
        clients: OAuthProtocolClients,
        users: UserQueries,
        memberships: OrganizationQueries,
    ) = OAuthClientAccess(database, clients, users, memberships)

    @Bean
    fun readOAuthConsent(
        database: DatabaseFactory,
        clients: OAuthProtocolClients,
        users: UserQueries,
    ) = ReadOAuthConsent(database, clients, users)

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
        database: DatabaseFactory,
        accounts: UserAccountAccess,
        approvals: ClientApprovals,
    ): OAuth2AuthorizationConsentService = ApprovalOAuth2AuthorizationConsentService(database, accounts, approvals)
}
