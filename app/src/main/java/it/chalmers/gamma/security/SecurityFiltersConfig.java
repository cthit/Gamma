package it.chalmers.gamma.security;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.CACHE;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;

import it.chalmers.gamma.adapter.secondary.jpa.user.TrustedUserDetailsRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.oauth2.ClaimsMapper;
import it.chalmers.gamma.app.oauth2.UserInfoMapper;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.security.api.ApiAuthenticationFilter;
import it.chalmers.gamma.security.api.ApiAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
public class SecurityFiltersConfig {

  @Order(1)
  @Bean
  public SecurityFilterChain authorizationServerSecurityFilterChain(
      HttpSecurity http, UserInfoMapper userInfoMapper) throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        new OAuth2AuthorizationServerConfigurer();

    http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        .with(
            authorizationServerConfigurer,
            authorizationServer ->
                authorizationServer
                    .authorizationEndpoint(
                        authorizationEndpoint ->
                            authorizationEndpoint.consentPage("/oauth2/consent"))
                    .oidc(
                        oidcConfigurer ->
                            oidcConfigurer.userInfoEndpoint(
                                userInfo -> userInfo.userInfoMapper(userInfoMapper))))
        .authorizeHttpRequests(authorization -> authorization.anyRequest().authenticated())
        .exceptionHandling(
            (exceptions) ->
                exceptions.defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login?authorizing"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
        .oauth2ResourceServer((resourceServer) -> resourceServer.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().oidcUserInfoEndpoint("/oauth2/userinfo").build();
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(ClaimsMapper claimsMapper) {
    return (context) -> {
      if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
        context
            .getClaims()
            .claims(
                (claims) ->
                    claims.putAll(
                        claimsMapper.generateClaims(
                            context.getAuthorizedScopes().stream()
                                .map(scope -> "SCOPE_" + scope)
                                .toList(),
                            UserId.valueOf(context.getAuthorization().getPrincipalName()))));
      }
    };
  }

  @Order(2)
  @Bean
  SecurityFilterChain externalSecurityFilterChain(
      HttpSecurity http,
      ApiKeyRepository apiKeyRepository,
      ClientRepository clientRepository,
      PasswordEncoder passwordEncoder)
      throws Exception {

    ApiAuthenticationProvider apiAuthenticationProvider =
        new ApiAuthenticationProvider(apiKeyRepository, clientRepository, passwordEncoder);

    RegexRequestMatcher regexRequestMatcher = new RegexRequestMatcher("\\/api/.+", null);
    http.securityMatcher(regexRequestMatcher)
        .addFilterBefore(
            new ApiAuthenticationFilter(new ProviderManager(apiAuthenticationProvider)),
            BasicAuthenticationFilter.class)
        .authorizeHttpRequests(authorization -> authorization.anyRequest().authenticated())
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Since only backends will call this
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(
            config ->
                config.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
    return http.build();
  }

  @Order(3)
  @Bean
  SecurityFilterChain imagesSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher(new RegexRequestMatcher("\\/images.+", null))
        .authorizeHttpRequests(authorization -> authorization.anyRequest().permitAll())
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable());
    return http.build();
  }

  /** Sets up the security for web interface */
  @Order(4)
  @Bean
  SecurityFilterChain webSecurityFilterChain(
      HttpSecurity http,
      PasswordEncoder passwordEncoder,
      UserJpaRepository userJpaRepository,
      AdminRepository adminRepository,
      ThrottlingService throttlingService)
      throws Exception {

    TrustedUserDetailsRepository trustedUserDetails =
        new TrustedUserDetailsRepository(userJpaRepository);

    DaoAuthenticationProvider userAuthenticationProvider =
        new DaoAuthenticationProvider(trustedUserDetails);
    userAuthenticationProvider.setPasswordEncoder(passwordEncoder);

    HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
    requestCache.setMatchingRequestParameterName(null);

    http.addFilterBefore(
            new LoginThrottlingFilter(throttlingService),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(
            new UpdateUserPrincipalFilter(trustedUserDetails, adminRepository),
            UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(HttpMethod.GET, "/img/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/js/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/css/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/webjars/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/login")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/login")
                    .anonymous()
                    .requestMatchers(HttpMethod.GET, "/activate-cid")
                    .anonymous()
                    .requestMatchers(HttpMethod.POST, "/activate-cid")
                    .anonymous()
                    .requestMatchers(HttpMethod.GET, "/email-sent")
                    .anonymous()
                    .requestMatchers(HttpMethod.GET, "/register")
                    .anonymous()
                    .requestMatchers(HttpMethod.POST, "/register")
                    .anonymous()
                    .requestMatchers(HttpMethod.GET, "/forgot-password")
                    .anonymous()
                    .requestMatchers(HttpMethod.POST, "/forgot-password")
                    .anonymous()
                    .requestMatchers(HttpMethod.GET, "/forgot-password/finalize")
                    .anonymous()
                    .requestMatchers(HttpMethod.POST, "/forgot-password/finalize")
                    .anonymous()
                    .requestMatchers(HttpMethod.GET, "/robots.txt")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(new LoginCustomizer())
        .logout(
            (logout) ->
                logout.addLogoutHandler(
                    new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(CACHE, COOKIES))))
        .authenticationProvider(userAuthenticationProvider)
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .cors(Customizer.withDefaults())
        .csrf((csrf) -> csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()))
        .requestCache(cacheConfig -> cacheConfig.requestCache(requestCache))
        .exceptionHandling(
            exceptionConfig ->
                exceptionConfig.accessDeniedHandler(
                    (request, response, accessDeniedException) -> response.sendRedirect("/")))
        .headers(
            headers ->
                headers.contentSecurityPolicy(
                    csp ->
                        csp.policyDirectives(
                            "default-src 'self'; object-src 'none'; frame-ancestors 'none'; frame-src 'none'; base-uri 'none';")));

    return http.build();
  }
}
