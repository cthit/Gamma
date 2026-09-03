package it.chalmers.gamma

import it.chalmers.gamma.apiaccess.ApiCredentialAuthenticator
import it.chalmers.gamma.throttling.FixedWindowThrottling
import it.chalmers.gamma.users.Cid
import it.chalmers.gamma.users.Email
import it.chalmers.gamma.users.GammaPrincipal
import it.chalmers.gamma.users.PlainTextPassword
import it.chalmers.gamma.users.UserAuthentication
import it.chalmers.gamma.users.UserId
import it.chalmers.gamma.users.UserIdentifier
import it.chalmers.gamma.users.UserStore
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.FactorGrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.context.SecurityContextHolderFilter
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.util.matcher.RegexRequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
class ApplicationSecurityConfiguration {
    @Bean
    fun userAuthenticationProvider(
        authentication: UserAuthentication,
        users: UserStore,
    ): AuthenticationProvider =
        object : AuthenticationProvider {
            override fun authenticate(request: Authentication): Authentication {
                val identifier =
                    request.name
                        .trim()
                        .lowercase()
                        .toUserIdentifier()
                val password = runCatching { PlainTextPassword(request.credentials.toString()) }.getOrNull()
                if (identifier == null || password == null) throw BadCredentialsException("Invalid credentials")
                val user =
                    authentication.authenticate(identifier, password)
                        ?: throw BadCredentialsException("Invalid credentials")
                val administrator = users.isAdministrator(user.id)
                val authorities =
                    buildList {
                        add(
                            FactorGrantedAuthority
                                .withAuthority(FactorGrantedAuthority.PASSWORD_AUTHORITY)
                                .issuedAt(Instant.now())
                                .build(),
                        )
                        if (administrator) add(SimpleGrantedAuthority("ROLE_ADMIN"))
                    }
                val principal = GammaPrincipal(user.id.value.toString(), user.nick.value, administrator)
                return UsernamePasswordAuthenticationToken(principal, null, authorities)
            }

            override fun supports(authentication: Class<*>): Boolean =
                UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
        }

    @Bean
    @Order(2)
    fun apiSecurityFilterChain(
        http: HttpSecurity,
        authenticator: ApiCredentialAuthenticator,
    ): SecurityFilterChain {
        http.securityMatcher(RegexRequestMatcher("\\/api/.+", null))
        http.addFilterBefore(ApiKeyAuthenticationFilter(authenticator), BasicAuthenticationFilter::class.java)
        http.authorizeHttpRequests { authorize -> authorize.anyRequest().authenticated() }
        http.csrf { csrf -> csrf.disable() }
        http.requestCache { requestCache -> requestCache.disable() }
        http.sessionManagement { sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        http.exceptionHandling { exceptions ->
            exceptions.authenticationEntryPoint(HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED))
        }
        return http.build()
    }

    @Bean
    @Order(3)
    fun imageSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.securityMatcher(RegexRequestMatcher("\\/images.+", null))
        http.authorizeHttpRequests { authorize -> authorize.anyRequest().permitAll() }
        http.csrf { csrf -> csrf.disable() }
        http.requestCache { requestCache -> requestCache.disable() }
        http.sessionManagement { sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        return http.build()
    }

    @Bean
    @Order(4)
    fun applicationSecurityFilterChain(
        http: HttpSecurity,
        userAuthenticationProvider: AuthenticationProvider,
        users: UserStore,
        throttling: FixedWindowThrottling,
    ): SecurityFilterChain {
        val requestCache = HttpSessionRequestCache().apply { setMatchingRequestParameterName("") }
        http.authenticationProvider(userAuthenticationProvider)
        http.addFilterAfter(
            SessionPrincipalRefreshFilter(users::sessionAccess),
            SecurityContextHolderFilter::class.java,
        )
        http.addFilterBefore(LoginThrottlingFilter(throttling), UsernamePasswordAuthenticationFilter::class.java)
        http.authorizeHttpRequests { authorize ->
            authorize
                .requestMatchers(
                    "/login",
                    "/activate-cid",
                    "/email-sent",
                    "/register",
                    "/forgot-password",
                    "/forgot-password/finalize",
                    "/account-deleted",
                    "/robots.txt",
                    "/error",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/webjars/**",
                    "/.well-known/**",
                    "/oauth2/**",
                    "/connect/logout",
                ).permitAll()
                .requestMatchers(
                    "/users/**",
                    "/admins",
                    "/gdpr",
                    "/allow-list/**",
                    "/activation-codes/**",
                    "/api-keys/**",
                    "/user-clients",
                    "/types/**",
                    "/throttling/**",
                ).hasRole("ADMIN")
                .requestMatchers(
                    "/clients",
                    "/clients/create",
                    "/clients/create/new-restriction",
                    "/clients/authority/**",
                    "/clients/*/authorities",
                    "/clients/*/new-authority",
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.GET,
                    "/groups/create",
                    "/groups/*/edit",
                    "/groups/new-member",
                    "/super-groups/create",
                    "/super-groups/*/edit",
                    "/posts/create",
                    "/posts/*/edit",
                ).hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/groups/create", "/super-groups", "/posts")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()
        }
        http.formLogin { login ->
            login
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false)
                .failureUrl("/login?error")
                .permitAll()
        }
        http.logout { logout ->
            logout
                .addLogoutHandler(
                    HeaderWriterLogoutHandler(
                        ClearSiteDataHeaderWriter(
                            ClearSiteDataHeaderWriter.Directive.CACHE,
                            ClearSiteDataHeaderWriter.Directive.COOKIES,
                        ),
                    ),
                ).logoutSuccessUrl("/login?logout")
        }
        http.exceptionHandling { exceptions -> exceptions.accessDeniedHandler(browserAccessDeniedHandler()) }
        http.sessionManagement { sessions ->
            sessions
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation { fixation -> fixation.newSession() }
        }
        http.csrf { csrf -> csrf.csrfTokenRequestHandler(XorCsrfTokenRequestAttributeHandler()) }
        http.requestCache { cache -> cache.requestCache(requestCache) }
        http.headers { headers ->
            headers.contentSecurityPolicy { policy ->
                policy.policyDirectives(
                    "default-src 'self'; object-src 'none'; frame-ancestors 'none'; " +
                        "frame-src 'none'; base-uri 'none';",
                )
            }
        }
        return http.build()
    }

    private fun browserAccessDeniedHandler() =
        AccessDeniedHandler { _, response, _ ->
            response.status = 403
            response.contentType = "text/html;charset=UTF-8"
            response.writer.write(
                "<!doctype html><html><body><main><h1>403 - Unauthorized</h1>" +
                    "<p>You are not authorized to view this page.</p></main></body></html>",
            )
        }
}

internal class SessionPrincipalRefreshFilter(
    private val loadSessionAccess: (UserId) -> it.chalmers.gamma.users.SessionAccess?,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal as? GammaPrincipal
        if (principal == null || !authentication.isAuthenticated) {
            filterChain.doFilter(request, response)
            return
        }

        val userId = runCatching { UserId.parse(principal.userId) }.getOrNull()
        val access = userId?.let(loadSessionAccess)
        if (access == null || access.locked) {
            SecurityContextHolder.clearContext()
            request.getSession(false)?.invalidate()
            filterChain.doFilter(request, response)
            return
        }

        val refreshedPrincipal = principal.copy(administrator = access.administrator)
        val refreshedAuthorities =
            authentication.authorities
                .filterNot { it.authority == "ROLE_ADMIN" }
                .toMutableList()
                .apply {
                    if (access.administrator) add(SimpleGrantedAuthority("ROLE_ADMIN"))
                }
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken
                .authenticated(
                    refreshedPrincipal,
                    authentication.credentials,
                    refreshedAuthorities,
                ).apply { details = authentication.details }

        filterChain.doFilter(request, response)
    }
}

private fun String.toUserIdentifier(): UserIdentifier? =
    runCatching {
        if ('@' in this) Email(this) else Cid(this)
    }.getOrNull()
