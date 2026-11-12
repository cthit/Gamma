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
import it.chalmers.gamma.app.signin.RecoveryKeyUse;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.api.ApiAuthenticationFilter;
import it.chalmers.gamma.security.api.ApiAuthenticationProvider;
import it.chalmers.gamma.security.signin.FactorCheckedAtStartProvider;
import it.chalmers.gamma.security.signin.HtmxAwareRedirects;
import it.chalmers.gamma.security.signin.PasskeyConfig;
import it.chalmers.gamma.security.signin.RecoveryKeyAuthenticationFilter;
import it.chalmers.gamma.security.signin.RecoveryKeyAuthenticationProvider;
import it.chalmers.gamma.security.signin.SignInAccessDeniedHandler;
import it.chalmers.gamma.security.signin.SignInAuthorizationManagers;
import it.chalmers.gamma.security.signin.SignInFactors;
import it.chalmers.gamma.security.signin.SignInSessionStrategy;
import it.chalmers.gamma.security.signin.SignInSetupSessions;
import it.chalmers.gamma.security.signin.SignInStepEntryPoint;
import it.chalmers.gamma.security.signin.TotpAuthenticationFilter;
import it.chalmers.gamma.security.signin.TotpAuthenticationProvider;
import it.chalmers.gamma.security.signin.UserBoundCreationOptionsRepository;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.webauthn.authentication.WebAuthnAuthenticationFilter;
import org.springframework.security.web.webauthn.authentication.WebAuthnAuthenticationProvider;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.WebAuthnRelyingPartyOperations;

@Configuration
@EnableMultiFactorAuthentication(authorities = {})
public class SecurityFiltersConfig {

  @Order(1)
  @Bean
  public SecurityFilterChain authorizationServerSecurityFilterChain(
      HttpSecurity http,
      UserInfoMapper userInfoMapper,
      SignInAuthorizationManagers signInAuthorizationManagers,
      AuthorizationServerSettings authorizationServerSettings,
      UserJpaRepository userJpaRepository,
      AdminRepository adminRepository)
      throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        new OAuth2AuthorizationServerConfigurer();

    http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        // The authorization endpoint uses the same session as the web interface. Check it here as
        // well, so that a user that has been locked can't authorize clients.
        .addFilterAfter(
            new UpdateUserPrincipalFilter(
                new TrustedUserDetailsRepository(userJpaRepository), adminRepository),
            SecurityContextHolderFilter.class)
        .with(
            authorizationServerConfigurer,
            authorizationServer ->
                authorizationServer
                    .authorizationServerMetadataEndpoint(metadataEndpoint ->
                        metadataEndpoint.authorizationServerMetadataCustomizer(
                            AuthorizationServerMetadataCustomizer::customize))
                    .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage("/oauth2/consent"))
                    .oidc(oidcConfigurer ->
                        oidcConfigurer
                            .providerConfigurationEndpoint(providerConfigurationEndpoint ->
                                providerConfigurationEndpoint.providerConfigurationCustomizer(
                                    AuthorizationServerMetadataCustomizer::customize))
                            .userInfoEndpoint(userInfo -> userInfo.userInfoMapper(userInfoMapper))))
        .authorizeHttpRequests(authorization ->
            authorization
                .requestMatchers(authorizationServerSettings.getAuthorizationEndpoint())
                .access(signInAuthorizationManagers.signedInUser())
                .anyRequest()
                .access(signInAuthorizationManagers.signedIn()))
        .exceptionHandling(exceptions -> {
          exceptions.defaultAuthenticationEntryPointFor(
              new LoginUrlAuthenticationEntryPoint("/login?authorizing"),
              new MediaTypeRequestMatcher(MediaType.TEXT_HTML));
          signInSteps(exceptions, new HttpSessionRequestCache(), signInAuthorizationManagers);
        })
        .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().oidcUserInfoEndpoint("/oauth2/userinfo").build();
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(ClaimsMapper claimsMapper) {
    return context -> {
      if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
        context
            .getClaims()
            .claims(claims ->
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
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Since only backends will call this
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(config ->
            config.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
    return http.build();
  }

  @Order(3)
  @Bean
  SecurityFilterChain imagesSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher(new RegexRequestMatcher("\\/images.+", null))
        .authorizeHttpRequests(authorization -> authorization.anyRequest().permitAll())
        .sessionManagement(sessionManagement ->
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
      UserRepository userRepository,
      AdminRepository adminRepository,
      TotpRepository totpRepository,
      RecoveryKeyUse recoveryKeyUse,
      ThrottlingService throttlingService,
      SignInAuthorizationManagers signIn,
      PublicKeyCredentialUserEntityRepository passkeyUsers,
      WebAuthnRelyingPartyOperations relyingPartyOperations)
      throws Exception {

    TrustedUserDetailsRepository trustedUserDetails =
        new TrustedUserDetailsRepository(userJpaRepository);

    DaoAuthenticationProvider userAuthenticationProvider =
        new DaoAuthenticationProvider(trustedUserDetails);
    userAuthenticationProvider.setPasswordEncoder(passwordEncoder);

    HttpSessionRequestCache requestCache = HtmxAwareRedirects.requestCache();

    http.setSharedObject(
        UserDetailsService.class, PasskeyConfig.passkeyUserDetailsService(userJpaRepository));

    http.addFilterBefore(
            new LoginThrottlingFilter(throttlingService),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(
            new TotpAuthenticationFilter(
                new ProviderManager(
                    new TotpAuthenticationProvider(totpRepository, throttlingService)),
                requestCache),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(
            new RecoveryKeyAuthenticationFilter(
                new ProviderManager(
                    new RecoveryKeyAuthenticationProvider(userRepository, recoveryKeyUse))),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(
            new UpdateUserPrincipalFilter(trustedUserDetails, adminRepository),
            UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(authorize ->
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
                .permitAll()
                .requestMatchers(HttpMethod.GET, TotpAuthenticationFilter.URL)
                .access(signIn.signingInWithTotp())
                .requestMatchers(HttpMethod.GET, "/login/confirm")
                .access(signIn.signingIn())
                .requestMatchers(HttpMethod.GET, RecoveryKeyAuthenticationFilter.URL)
                .permitAll()
                .requestMatchers(
                    SignInSetupSessions.SETUP_URL, SignInSetupSessions.SETUP_URL + "/**")
                .access(signIn.settingUpSignIn())
                .requestMatchers(HttpMethod.POST, "/webauthn/register")
                .access(signIn.mayAddPasskey())
                .requestMatchers("/webauthn/register/**")
                .denyAll()
                .requestMatchers("/me/security", "/me/security/**")
                .access(signIn.recentlySignedIn())
                .requestMatchers("/me/edit-password", "/users/*/reset-sign-in")
                .access(signIn.recentlySignedIn())
                .requestMatchers("/me/edit-email", "/delete-your-account")
                .access(signIn.recentlySignedInIfPasswordless())
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
                .requestMatchers(HttpMethod.POST, "/forgot-password/finalize/passkey")
                .anonymous()
                .requestMatchers(HttpMethod.GET, "/robots.txt")
                .permitAll()
                // The error page must be reachable without logging in, otherwise errors
                // are turned into redirects to the login page.
                .dispatcherTypeMatchers(DispatcherType.ERROR)
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/error")
                .permitAll()
                .anyRequest()
                .access(signIn.signedIn()))
        .formLogin(new LoginCustomizer(requestCache))
        .webAuthn(webAuthn ->
            webAuthn
                .disableDefaultRegistrationPage(true)
                .creationOptionsRepository(new UserBoundCreationOptionsRepository(passkeyUsers))
                .withObjectPostProcessor(
                    new ObjectPostProcessor<WebAuthnAuthenticationFilter>() {
                      @Override
                      public <O extends WebAuthnAuthenticationFilter> O postProcess(O filter) {
                        filter.setSessionAuthenticationStrategy(SignInSessionStrategy.create());
                        filter.setAuthenticationManager(
                            new ProviderManager(
                                new FactorCheckedAtStartProvider(
                                    new WebAuthnAuthenticationProvider(
                                        relyingPartyOperations,
                                        PasskeyConfig.passkeyUserDetailsService(userJpaRepository)),
                                    SignInFactors.PASSKEY)));
                        return filter;
                      }
                    }))
        .logout(logout ->
            logout.addLogoutHandler(
                new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(CACHE, COOKIES))))
        .authenticationProvider(
            new FactorCheckedAtStartProvider(userAuthenticationProvider, SignInFactors.PASSWORD))
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()))
        .requestCache(cacheConfig -> cacheConfig.requestCache(requestCache))
        .exceptionHandling(exceptions -> signInSteps(exceptions, requestCache, signIn))
        .headers(headers ->
            headers.contentSecurityPolicy(csp ->
                csp.policyDirectives(
                    "default-src 'self'; object-src 'none'; frame-ancestors 'none'; frame-src 'none'; base-uri 'none';")));

    return http.build();
  }

  private static void signInSteps(
      ExceptionHandlingConfigurer<HttpSecurity> exceptions,
      RequestCache requestCache,
      SignInAuthorizationManagers signIn) {
    AuthenticationEntryPoint totp =
        new SignInStepEntryPoint(TotpAuthenticationFilter.URL, requestCache);
    AuthenticationEntryPoint confirm = new SignInStepEntryPoint("/login/confirm", requestCache);

    exceptions
        .defaultDeniedHandlerForMissingAuthority(
            entryPoint -> entryPoint.addEntryPointFor(totp, AnyRequestMatcher.INSTANCE),
            SignInFactors.TOTP)
        .defaultDeniedHandlerForMissingAuthority(
            entryPoint -> entryPoint.addEntryPointFor(confirm, AnyRequestMatcher.INSTANCE),
            SignInFactors.PASSKEY)
        .defaultDeniedHandlerForMissingAuthority(
            entryPoint -> entryPoint.addEntryPointFor(confirm, AnyRequestMatcher.INSTANCE),
            SignInFactors.PASSWORD)
        .defaultAccessDeniedHandlerFor(
            new SignInAccessDeniedHandler(signIn), AnyRequestMatcher.INSTANCE);
  }
}
