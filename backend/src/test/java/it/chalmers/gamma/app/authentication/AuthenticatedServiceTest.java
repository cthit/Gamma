package it.chalmers.gamma.app.authentication;

import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientSecret;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.RedirectUrl;
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.bootstrap.BootstrapAuthenticated;
import it.chalmers.gamma.security.ApiKeyAuthentication;
import it.chalmers.gamma.security.authentication.ApiAuthenticated;
import it.chalmers.gamma.security.authentication.ExternalUserAuthenticated;
import it.chalmers.gamma.security.authentication.GammaSecurityContextUtils;
import it.chalmers.gamma.security.authentication.InternalUserAuthenticated;
import it.chalmers.gamma.security.authentication.LocalRunnerAuthenticated;
import it.chalmers.gamma.security.authentication.LockedInternalUserAuthenticated;
import it.chalmers.gamma.security.authentication.Unauthenticated;
import it.chalmers.gamma.security.user.UserDetailsProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class AuthenticatedServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ClientRepository clientRepository;

    private static final User normalUser = new User(
            UserId.generate(),
            new Cid("edcba"),
            new Nick("TheUser"),
            new FirstName("Something1"),
            new LastName("Somethingsson1"),
            new AcceptanceYear(2021),
            Language.EN,
            new UserExtended(
                    new Email("edcba@chalmers.it"),
                    0,
                    true,
                    false,
                    false,
                    null
            )
    );

    private static final User lockedUser = normalUser.with()
            .id(UserId.generate())
            .extended(normalUser.extended().withLocked(true))
            .build();

    private static final ApiKey clientApiKey = new ApiKey(
            ApiKeyId.generate(),
            new PrettyName("My api key"),
            new Text(
                    "Det här är min api nyckel",
                    "This is my api key"
            ),
            ApiKeyType.CLIENT,
            ApiKeyToken.generate()
    );

    private static final Client client = new Client(
            ClientUid.generate(),
            ClientId.generate(),
            ClientSecret.generate(),
            new RedirectUrl("https://mat.chalmers.it"),
            new PrettyName("Mat client"),
            new Text(
                    "Det här är mat klienten",
                    "This is the mat client"
            ),
            Collections.emptyList(),
            Collections.singletonList(Scope.PROFILE),
            Collections.singletonList(normalUser),
            Optional.of(clientApiKey)
    );

    private static final ApiKey chalmersitApi = new ApiKey(
            ApiKeyId.generate(),
            new PrettyName("chalmers.it api key"),
            new Text(
                    "chalmers.it api nyckel",
                    "chalmers.it api key"
            ),
            ApiKeyType.INFO,
            ApiKeyToken.generate()
    );

    @Test
    @WithMockInternalAuthenticated
    public void Given_UserDetailsProxy_Expect_getAuthentication_ToReturn_InternalUserAuthenticated() {
        given(userRepository.get(normalUser.id()))
                .willReturn(Optional.of(normalUser));

        assertThat(GammaSecurityContextUtils.getAuthentication())
                .isInstanceOfSatisfying(
                        InternalUserAuthenticated.class,
                        internal ->
                                assertThat(internal.get())
                                        .isEqualTo(normalUser)
                );
    }

    @Test
    @WithMockInternalAuthenticated
    public void Given_UserDetailsProxyNotAcceptedUserAgreement_Expect_getAuthentication_ToReturn_InternalUserAuthenticated() {
        User notAcceptingUser = normalUser.withExtended(normalUser.extended().withAcceptedUserAgreement(false));

        given(userRepository.get(notAcceptingUser.id()))
                .willReturn(Optional.of(notAcceptingUser));

        assertThat(GammaSecurityContextUtils.getAuthentication())
                .isInstanceOfSatisfying(
                        LockedInternalUserAuthenticated.class,
                        internal ->
                                assertThat(internal.get())
                                        .isEqualTo(notAcceptingUser)
                );
    }

    @Test
    @WithMockInternalAuthenticated(locked = true)
    public void Given_UserDetailsProxyThatIsLocked_Expect_getAuthentication_ToReturn_LockedInternalUserAuthenticated() {
        given(userRepository.get(lockedUser.id()))
                .willReturn(Optional.of(lockedUser));

        assertThat(GammaSecurityContextUtils.getAuthentication())
                .isInstanceOfSatisfying(
                        LockedInternalUserAuthenticated.class,
                        internal ->
                                assertThat(internal.get())
                                        .isEqualTo(lockedUser)
                );
    }


    @Test
    @WithMockExternalAuthenticated
    public void Given_Jwt_Expect_getAuthentication_ToReturn_ExternalAuthenticated() {
        given(userRepository.get(normalUser.id()))
                .willReturn(Optional.of(normalUser));

        assertThat(GammaSecurityContextUtils.getAuthentication())
                .isInstanceOfSatisfying(
                        ExternalUserAuthenticated.class,
                        external ->
                                assertThat(external.get())
                                        .isEqualTo(normalUser)
                );
    }

    @Test
    @WithMockApiAuthenticated("client")
    public void Given_ClientApiKeyToken_Expect_getAuthentication_ToReturn_ApiKeyAuthenticated() {
        given(this.apiKeyRepository.getByToken(clientApiKey.apiKeyToken()))
                .willReturn(Optional.of(clientApiKey));
        given(this.clientRepository.getByApiKey(clientApiKey.apiKeyToken()))
                .willReturn(Optional.of(client));

        assertThat(GammaSecurityContextUtils.getAuthentication())
                .isInstanceOfSatisfying(
                        ApiAuthenticated.class,
                        api -> {
                            assertThat(api.get())
                                    .isEqualTo(clientApiKey);
                            assertThat(api.getClient())
                                    .get()
                                    .isEqualTo(client);
                        }
                );
    }

    @Test
    @WithMockApiAuthenticated("chalmersit")
    public void Given_ChalmersitApiKeyToken_Expect_getAuthentication_ToReturn_ApiKeyAuthenticated() {
        given(this.apiKeyRepository.getByToken(chalmersitApi.apiKeyToken()))
                .willReturn(Optional.of(chalmersitApi));

        assertThat(GammaSecurityContextUtils.getAuthentication())
                .isInstanceOfSatisfying(
                        ApiAuthenticated.class,
                        api -> {
                            assertThat(api.get())
                                    .isEqualTo(chalmersitApi);
                        }
                );
    }

    @Test
    @WithMockBootstrapAuthenticated
    public void Given_BootstrapAuthenticated_Expect_getAuthentication_ToReturn_LocalRunnerAuthenticated() {
        assertThat(GammaSecurityContextUtils.getAuthentication())
                .isInstanceOf(LocalRunnerAuthenticated.class);
    }

    @Test
    @WithAnonymousUser
    public void Given_AnonymousUser_Expect_getAuthentication_ToReturn_Unauthenticated() {
        assertThat(GammaSecurityContextUtils.getAuthentication())
                .isInstanceOf(Unauthenticated.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithInternalAuthenticatedSecurityContextFactory.class)
    private @interface WithMockInternalAuthenticated {
        boolean locked() default false;
    }

    private static class WithInternalAuthenticatedSecurityContextFactory implements WithSecurityContextFactory<WithMockInternalAuthenticated> {
        @Override
        public SecurityContext createSecurityContext(WithMockInternalAuthenticated annotation) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            User user = annotation.locked() ? lockedUser : normalUser;

            UserDetailsProxy userDetailsProxy = new UserDetailsProxy(user.id().value());
            userDetailsProxy.set(user, Collections.emptyList(), "{noop}value");
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetailsProxy,
                    null,
                    userDetailsProxy.getAuthorities()
            );
            context.setAuthentication(auth);
            return context;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithExternalAuthenticatedSecurityContextFactory.class)
    private @interface WithMockExternalAuthenticated { }

    private static class WithExternalAuthenticatedSecurityContextFactory implements WithSecurityContextFactory<WithMockExternalAuthenticated> {
        @Override
        public SecurityContext createSecurityContext(WithMockExternalAuthenticated annotation) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            Instant issued = Instant.now();
            Instant expires = issued.plus(5, ChronoUnit.MINUTES);

            Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "RS256")
                    .subject(normalUser.id().value().toString())
                    .issuedAt(issued)
                    .expiresAt(expires)
                    .build();

            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
            jwtAuthenticationToken.setAuthenticated(true);
            context.setAuthentication(jwtAuthenticationToken);

            return context;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithApiAuthenticatedSecurityContextFactory.class)
    private @interface WithMockApiAuthenticated {
        String value();
    }

    private static class WithApiAuthenticatedSecurityContextFactory implements WithSecurityContextFactory<WithMockApiAuthenticated> {
        @Override
        public SecurityContext createSecurityContext(WithMockApiAuthenticated annotation) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            ApiKey apiKey = switch (annotation.value()) {
                case "chalmersit" -> chalmersitApi;
                case "client" -> clientApiKey;
                default -> throw new IllegalStateException();
            };

            ApiKeyAuthentication apiKeyAuthentication = new ApiKeyAuthentication(apiKey, client);
            context.setAuthentication(apiKeyAuthentication);

            return context;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithBootstrapSecurityContextFactory.class)
    private @interface WithMockBootstrapAuthenticated { }

    private static class WithBootstrapSecurityContextFactory implements WithSecurityContextFactory<WithMockBootstrapAuthenticated> {
        @Override
        public SecurityContext createSecurityContext(WithMockBootstrapAuthenticated annotation) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            context.setAuthentication(new BootstrapAuthenticated());

            return context;
        }
    }

}