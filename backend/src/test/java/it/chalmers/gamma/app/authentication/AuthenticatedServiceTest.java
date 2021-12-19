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
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.client.domain.WebServerRedirectUrl;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.Password;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.userdetails.UserDetailsProxy;
import it.chalmers.gamma.security.ApiKeyAuthentication;
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
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class AuthenticatedServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AuthenticatedService authenticatedService;

    private static final User normalUser = new User(
            UserId.generate(),
            0,
            new Cid("edcba"),
            new Email("edcba@chalmers.it"),
            Language.EN,
            new Nick("TheUser"),
            new Password("{noop}password321"),
            new FirstName("Something1"),
            new LastName("Somethingsson1"),
            Instant.now(),
            new AcceptanceYear(2021),
            true,
            false,
            Optional.empty()
    );

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
            new WebServerRedirectUrl("https://mat.chalmers.it"),
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
            ApiKeyType.CHALMERSIT,
            ApiKeyToken.generate()
    );

    @Test
    @WithMockInternalAuthenticated
    public void Given_UserDetailsProxy_Expect_getAuthentication_ToReturn_InternalAuthenticated() {
        given(userRepository.get(normalUser.id()))
                .willReturn(Optional.of(normalUser));

        assertThat(this.authenticatedService.getAuthenticated())
                .isInstanceOfSatisfying(
                        InternalUserAuthenticated.class,
                        internal ->
                                assertThat(internal.get())
                                        .isEqualTo(normalUser)
                );
    }

    @Test
    @WithMockExternalAuthenticated
    public void Given_Jwt_Expect_getAuthentication_ToReturn_ExternalAuthenticated() {
        given(userRepository.get(normalUser.id()))
                .willReturn(Optional.of(normalUser));

        assertThat(this.authenticatedService.getAuthenticated())
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

        assertThat(this.authenticatedService.getAuthenticated())
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

        assertThat(this.authenticatedService.getAuthenticated())
                .isInstanceOfSatisfying(
                        ApiAuthenticated.class,
                        api -> {
                            assertThat(api.get())
                                    .isEqualTo(chalmersitApi);
                        }
                );
    }

    @Test
    @WithAnonymousUser
    public void Given_AnonymousUser_Expect_getAuthentication_ToReturn_Unauthenticated() {
        assertThat(this.authenticatedService.getAuthenticated())
                .isInstanceOf(Unauthenticated.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = WithInternalAuthenticatedSecurityContextFactory.class)
    private @interface WithMockInternalAuthenticated { }

    private static class WithInternalAuthenticatedSecurityContextFactory implements WithSecurityContextFactory<WithMockInternalAuthenticated> {
        @Override
        public SecurityContext createSecurityContext(WithMockInternalAuthenticated annotation) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            UserDetailsProxy userDetailsProxy = new UserDetailsProxy(normalUser, Collections.emptyList());
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetailsProxy,
                    "password",
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

            ApiKeyToken token = switch (annotation.value()) {
                case "chalmersit" -> chalmersitApi.apiKeyToken();
                case "client" -> clientApiKey.apiKeyToken();
                default -> throw new IllegalStateException();
            };

            ApiKeyAuthentication apiKeyAuthentication = new ApiKeyAuthentication(
                    token,
                    AuthorityUtils.NO_AUTHORITIES
            );

            context.setAuthentication(apiKeyAuthentication);

            return context;
        }
    }


}