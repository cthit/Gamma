// Spring exposes security collaborators as explicit bean-method parameters.
package it.chalmers.gamma.oauth.server

import it.chalmers.gamma.oauth.ClientUid
import it.chalmers.gamma.oauth.OAuthClaimDecisions
import it.chalmers.gamma.oauth.OAuthProtocolClients
import it.chalmers.gamma.organization.OrganizationStore
import it.chalmers.gamma.organization.SuperGroupId
import it.chalmers.gamma.platform.core.UserId
import it.chalmers.gamma.users.UserStore
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.FactorGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.web.filter.OncePerRequestFilter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.function.Consumer

@Configuration(proxyBeanMethods = false)
internal class AuthorizationServerHttpSecurityConfiguration(
    private val clients: OAuthProtocolClients,
    private val users: UserStore,
    private val memberships: OrganizationStore,
    private val claimDecisions: OAuthClaimDecisions,
    private val issuer: OAuthIssuer,
) {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(
        http: HttpSecurity,
        registeredClients: RegisteredClientRepository,
    ): SecurityFilterChain {
        http.oauth2AuthorizationServer { authorizationServer ->
            http.securityMatcher(authorizationServer.endpointsMatcher)
            authorizationServer
                .authorizationServerMetadataEndpoint { endpoint ->
                    endpoint.authorizationServerMetadataCustomizer { metadata ->
                        metadata.grantTypes { grantTypes ->
                            grantTypes.retainAll(SUPPORTED_AUTHORIZATION_GRANT_TYPES)
                        }
                    }
                }.authorizationEndpoint { endpoint ->
                    endpoint.consentPage("/oauth2/consent")
                    endpoint.authenticationProviders { providers ->
                        val provider =
                            providers
                                .filterIsInstance<OAuth2AuthorizationCodeRequestAuthenticationProvider>()
                                .single()
                        provider.setAuthenticationValidator(
                            OAuth2AuthorizationCodeRequestAuthenticationValidator()
                                .andThen(
                                    restrictedClientValidator(
                                        clients,
                                        users,
                                        memberships,
                                    ),
                                ),
                        )
                        provider.setAuthorizationConsentRequired { context ->
                            val requestedScopes = checkNotNull(context.authorizationRequest).scopes
                            context.registeredClient.clientSettings.isRequireAuthorizationConsent &&
                                context.authorizationConsent?.scopes?.containsAll(requestedScopes) != true
                        }
                    }
                }.oidc { oidc ->
                    oidc
                        .providerConfigurationEndpoint { endpoint ->
                            endpoint.providerConfigurationCustomizer { metadata ->
                                metadata.grantTypes { grantTypes ->
                                    grantTypes.retainAll(SUPPORTED_AUTHORIZATION_GRANT_TYPES)
                                }
                            }
                        }.userInfoEndpoint { endpoint ->
                            endpoint.userInfoMapper { context ->
                                val authorizedClaims =
                                    claimDecisions.claims(
                                        UserId.parse(context.authorization.principalName),
                                        context.accessToken.scopes,
                                    )
                                checkNotNull(authorizedClaims) {
                                    "Authorized user no longer exists or is locked"
                                }
                                val claims =
                                    context.authorization.accessToken
                                        ?.claims
                                        ?.toMutableMap() ?: mutableMapOf()
                                claims.putAll(
                                    authorizedClaims.toOidcClaimValues(
                                        issuer.publicBaseUrl,
                                    ),
                                )
                                OidcUserInfo(claims)
                            }
                        }.logoutEndpoint {
                            // Spring Authorization Server validates the RP and id_token_hint before
                            // invalidating Gamma's browser session.
                        }
                }
        }
        http.authorizeHttpRequests { authorize ->
            authorize.anyRequest().authenticated()
        }
        http.exceptionHandling { exceptions ->
            exceptions.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login?authorizing"))
        }
        http.addFilterBefore(
            OidcAuthorizationRequestInteractionFilter(registeredClients),
            AuthorizationFilter::class.java,
        )
        return http.build()
    }
}

private class OidcAuthorizationRequestInteractionFilter(
    private val registeredClients: RegisteredClientRepository,
) : OncePerRequestFilter() {
    private val requestCache = HttpSessionRequestCache()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.method != "GET" || request.servletPath != "/oauth2/authorize"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val prompts =
            request
                .getParameter("prompt")
                .orEmpty()
                .split(' ')
                .filter(String::isNotBlank)
                .toSet()
        val authentication = SecurityContextHolder.getContext().authentication
        val authenticated = authentication?.isAuthenticated == true && authentication !is AnonymousAuthenticationToken

        if (!authenticated && "none" in prompts) {
            sendLoginRequired(request, response)
            return
        }

        if (authenticated && requiresFreshLogin(request, prompts, authentication)) {
            val session = request.getSession(true)
            val state = request.getParameter("state").orEmpty()
            if (session.getAttribute(REAUTHENTICATED_STATE) == state) {
                session.removeAttribute(REAUTHENTICATED_STATE)
            } else {
                session.setAttribute(REAUTHENTICATED_STATE, state)
                requestCache.saveRequest(request, response)
                response.sendRedirect(request.contextPath + "/login?authorizing")
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun requiresFreshLogin(
        request: HttpServletRequest,
        prompts: Set<String>,
        authentication: Authentication,
    ): Boolean {
        if ("login" in prompts) return true
        val maximumAge = request.getParameter("max_age")?.toLongOrNull() ?: return false
        val authenticatedAt =
            authentication.authorities
                .filterIsInstance<FactorGrantedAuthority>()
                .firstOrNull { it.authority == FactorGrantedAuthority.PASSWORD_AUTHORITY }
                ?.issuedAt ?: return true
        return !authenticatedAt.plusSeconds(maximumAge).isAfter(Instant.now())
    }

    private fun sendLoginRequired(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val redirectUri = request.getParameter("redirect_uri")
        val client = request.getParameter("client_id")?.let(registeredClients::findByClientId)
        if (redirectUri.isNullOrBlank() || client == null || redirectUri !in client.redirectUris) {
            response.sendError(400)
            return
        }
        val separator = if ('?' in redirectUri) '&' else '?'
        val state = request.getParameter("state")
        val query =
            buildList {
                add("error=login_required")
                if (!state.isNullOrBlank()) add("state=${encode(state)}")
            }.joinToString("&")
        response.sendRedirect("$redirectUri$separator$query")
    }

    private fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8)

    private companion object {
        const val REAUTHENTICATED_STATE = "SPRING_SECURITY_OIDC_REAUTHENTICATED_STATE"
    }
}

private fun restrictedClientValidator(
    clients: OAuthProtocolClients,
    users: UserStore,
    memberships: OrganizationStore,
): Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> =
    Consumer { context ->
        @Suppress("CastNullableToNonNullableType")
        val authorizationRequest =
            context.get<Authentication>(Authentication::class.java)
                as OAuth2AuthorizationCodeRequestAuthenticationToken
        val userAuthentication = authorizationRequest.principal as? Authentication
        if (userAuthentication?.isAuthenticated != true) return@Consumer
        val userId = runCatching { UserId.parse(userAuthentication.name) }.getOrNull() ?: return@Consumer
        val clientUid = ClientUid.parse(context.registeredClient.id)
        val user = users.findUser(userId)
        val restrictedSuperGroups = clients.restrictedSuperGroupIds(clientUid)
        val allowed =
            user != null &&
                !user.locked &&
                (
                    restrictedSuperGroups.isEmpty() ||
                        memberships.isMemberOfAnySuperGroup(
                            userId,
                            restrictedSuperGroups.mapTo(mutableSetOf(), ::SuperGroupId),
                        )
                )
        if (!allowed) {
            throw OAuth2AuthorizationCodeRequestAuthenticationException(
                OAuth2Error(
                    OAuth2ErrorCodes.ACCESS_DENIED,
                    "The authenticated user does not satisfy this client's organization restrictions",
                    null,
                ),
                authorizationRequest,
            )
        }
    }
