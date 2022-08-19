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
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.bootstrap.BootstrapAuthenticated;
import it.chalmers.gamma.security.api.ApiAuthenticationToken;
import it.chalmers.gamma.security.principal.ApiPrincipal;
import it.chalmers.gamma.security.principal.GammaSecurityContextUtils;
import it.chalmers.gamma.security.principal.LocalRunnerPrincipal;
import it.chalmers.gamma.security.principal.UnauthenticatedPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

    private static final GammaUser normalUser = new GammaUser(
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

    private static final GammaUser lockedUser = normalUser.with()
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
    @WithMockApiAuthenticated("client")
    public void Given_ClientApiKeyToken_Expect_getAuthentication_ToReturn_ApiKeyAuthenticated() {
        given(this.apiKeyRepository.getByToken(clientApiKey.apiKeyToken()))
                .willReturn(Optional.of(clientApiKey));
        given(this.clientRepository.getByApiKey(clientApiKey.apiKeyToken()))
                .willReturn(Optional.of(client));

        assertThat(GammaSecurityContextUtils.getPrincipal())
                .isInstanceOfSatisfying(
                        ApiPrincipal.class,
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

        assertThat(GammaSecurityContextUtils.getPrincipal())
                .isInstanceOfSatisfying(
                        ApiPrincipal.class,
                        api -> {
                            assertThat(api.get())
                                    .isEqualTo(chalmersitApi);
                        }
                );
    }

    @Test
    @WithMockBootstrapAuthenticated
    public void Given_BootstrapAuthenticated_Expect_getAuthentication_ToReturn_LocalRunnerAuthenticated() {
        assertThat(GammaSecurityContextUtils.getPrincipal())
                .isInstanceOf(LocalRunnerPrincipal.class);
    }

    @Test
    @WithAnonymousUser
    public void Given_AnonymousUser_Expect_getAuthentication_ToReturn_Unauthenticated() {
        assertThat(GammaSecurityContextUtils.getPrincipal())
                .isInstanceOf(UnauthenticatedPrincipal.class);
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

//            ApiAuthenticationToken apiAuthenticationToken = new ApiAuthenticationToken(apiKey, client);
//            context.setAuthentication(apiAuthenticationToken);
//
//            return context;
            throw new UnsupportedOperationException("Not supported yet with api refactor");
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