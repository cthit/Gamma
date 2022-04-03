package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientId;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.client.domain.ClientSecret;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.RedirectUrl;
import it.chalmers.gamma.app.client.domain.Scope;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.user.PasswordConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static it.chalmers.gamma.utils.DomainUtils.addAll;
import static it.chalmers.gamma.utils.DomainUtils.asSaved;
import static it.chalmers.gamma.utils.DomainUtils.defaultSettings;
import static it.chalmers.gamma.utils.DomainUtils.removeUserExtended;
import static it.chalmers.gamma.utils.DomainUtils.u1;
import static it.chalmers.gamma.utils.DomainUtils.u2;
import static it.chalmers.gamma.utils.DomainUtils.u4;
import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedAsAdminUser;
import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedAsClientWithApi;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@Import({ClientRepositoryAdapter.class,
        ClientEntityConverter.class,
        UserRepositoryAdapter.class,
        PasswordConfiguration.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        ApiKeyRepositoryAdapter.class,
        ApiKeyEntityConverter.class,
        AuthorityLevelRepositoryAdapter.class,
        AuthorityLevelEntityConverter.class,
        SuperGroupEntityConverter.class,
        PostEntityConverter.class,
        UserEntityConverter.class,
        SettingsRepositoryAdapter.class})
public class ClientEntityIntegrationTests extends AbstractEntityIntegrationTests {

    @Autowired
    private ClientRepositoryAdapter clientRepositoryAdapter;
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityLevelRepository authorityLevelRepository;
    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    public void setSettings() {
        this.settingsRepository.setSettings(defaultSettings);
    }

    @Test
    public void Given_AValidClient_Expect_save_To_Work() {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithScopes_Expect_save_To_Work() {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                List.of(Scope.PROFILE, Scope.EMAIL),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithApiKey_Expect_save_To_Work() {
        ApiKey apiKey = new ApiKey(
                ApiKeyId.generate(),
                new PrettyName("Mat"),
                new Text(
                        "Api nyckel för mat",
                        "Api key for mat"
                ),
                ApiKeyType.CLIENT,
                ApiKeyToken.generate()
        );

        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.of(apiKey)
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithRestrictions_Expect_save_To_Work() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        authorityLevelRepository.create(new AuthorityLevelName("admin"));
        setAuthenticatedAsAdminUser(userRepository, null);

        addAll(userRepository, u1);
        authorityLevelRepository.save(new AuthorityLevel(
                new AuthorityLevelName("admin"),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(removeUserExtended(u1))
        ));

        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                List.of(new AuthorityLevelName("admin")),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithApprovedUsers_Expect_save_To_Work() {
        GammaUser user = setAuthenticatedAsAdminUser(userRepository, authorityLevelRepository);

        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        setAuthenticatedAsClientWithApi(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);

        setAuthenticatedAsAdminUser(user);

        addAll(userRepository, u1, u2, u4);

        Client newClient2 = newClient.withApprovedUsers(
                List.of(u1, u2, u4)
        );

        this.clientRepositoryAdapter.save(newClient2);

        setAuthenticatedAsClientWithApi(newClient);

        Client savedClient2 = this.clientRepositoryAdapter.get(uid).orElseThrow();

        //No u2 since they are locked
        assertThat(savedClient2)
                .isEqualTo(newClient2.withApprovedUsers(List.of(u1, u4)));
    }

    @Test
    public void Given_AValidClient_Expect_save_And_delete_To_Work() throws ClientRepository.ClientNotFoundException {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        assertThat(this.clientRepositoryAdapter.getAll())
                .containsExactlyInAnyOrder(newClient);

        this.clientRepositoryAdapter.delete(uid);

        assertThat(this.clientRepositoryAdapter.getAll())
                .isEmpty();
    }

    @Test
    public void Given_ClientWithApiKey_Expect_delete_To_DeleteBoth() throws ClientRepository.ClientNotFoundException {
        ApiKey apiKey = new ApiKey(
                ApiKeyId.generate(),
                new PrettyName("Mat"),
                new Text(
                        "Api nyckel för mat",
                        "Api key for mat"
                ),
                ApiKeyType.CLIENT,
                ApiKeyToken.generate()
        );

        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.of(apiKey)
        );

        this.clientRepositoryAdapter.save(newClient);

        assertThat(this.clientRepositoryAdapter.get(uid))
                .get().isEqualTo(newClient);

        this.clientRepositoryAdapter.delete(uid);
        assertThat(this.clientRepositoryAdapter.get(uid))
                .isEmpty();
        assertThat(this.apiKeyRepository.getById(apiKey.id()))
                .isEmpty();
    }

    @Test
    public void Given_InvalidClient_Expect_delete_To_Throw() {
        assertThatExceptionOfType(ClientRepository.ClientNotFoundException.class)
                .isThrownBy(() -> this.clientRepositoryAdapter.delete(ClientUid.generate()));
    }

    @Test
    public void Given_AValidClient_Expect_get_WithClientId_To_Work() {
        ClientId clientId = ClientId.generate();
        Client newClient = new Client(
                ClientUid.generate(),
                clientId,
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(clientId).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_ClientWithApiKey_Expect_DeletingApiKey_To_Work() throws ApiKeyRepository.ApiKeyNotFoundException {
        ApiKey apiKey = new ApiKey(
                ApiKeyId.generate(),
                new PrettyName("Mat"),
                new Text(
                        "Api nyckel för mat",
                        "Api key for mat"
                ),
                ApiKeyType.CLIENT,
                ApiKeyToken.generate()
        );

        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.of(apiKey)
        );

        this.clientRepositoryAdapter.save(newClient);

        this.apiKeyRepository.delete(apiKey.id());

        Client retrievedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(retrievedClient.clientApiKey())
                .isEmpty();
        assertThat(apiKeyRepository.getById(apiKey.id()))
                .isEmpty();
    }

    @Test
    public void Given_ClientWithInvalidRestriction_Expect_save_To_Throw() {
        Client newClient = new Client(
                ClientUid.generate(),
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                List.of(new AuthorityLevelName("test")),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        assertThatExceptionOfType(ClientRepository.AuthorityLevelNotFoundRuntimeException.class)
                .isThrownBy(() -> this.clientRepositoryAdapter.save(newClient));
    }

    @Test
    public void Given_ClientWithInvalidUser_Expect_save_To_Throw() {
        Client newClient = new Client(
                ClientUid.generate(),
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(u1),
                Optional.empty()
        );

        assertThatExceptionOfType(ClientRepository.UserNotFoundRuntimeException.class)
                .isThrownBy(() -> clientRepositoryAdapter.save(newClient));
    }

    @Test
    public void Given_SameClientId_Expect_save_To_Work() {
        Client client = new Client(
                ClientUid.generate(),
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Optional.empty()
        );

        this.clientRepositoryAdapter.save(client);

        assertThatExceptionOfType(ClientRepository.ClientIdAlreadyExistsRuntimeException.class)
                .isThrownBy(() -> this.clientRepositoryAdapter.save(client.withClientUid(ClientUid.generate())));
    }

    @Test
    public void Given_UserThatHasApprovedClients_Expect_getClientsByUserApproved_To_Work() {
        setAuthenticatedAsAdminUser(userRepository, authorityLevelRepository);

        addAll(userRepository, u1);

        Client client = new Client(
                ClientUid.generate(),
                ClientId.generate(),
                ClientSecret.generate(),
                new RedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(asSaved(u1)),
                Optional.empty()
        );

        Client client2 = client.with()
                .clientId(ClientId.generate())
                .clientUid(ClientUid.generate())
                .build();
        Client client3 = client.with()
                .clientId(ClientId.generate())
                .clientUid(ClientUid.generate())
                .approvedUsers(Collections.emptyList())
                .build();
        Client client4 = client.with()
                .clientId(ClientId.generate())
                .clientUid(ClientUid.generate())
                .build();
        Client client5 = client.with()
                .clientId(ClientId.generate())
                .clientUid(ClientUid.generate())
                .build();

        this.clientRepositoryAdapter.save(client);
        this.clientRepositoryAdapter.save(client2);
        this.clientRepositoryAdapter.save(client3);
        this.clientRepositoryAdapter.save(client4);
        this.clientRepositoryAdapter.save(client5);

        assertThat(this.clientRepositoryAdapter.getClientsByUserApproved(u1.id()))
                .containsExactlyInAnyOrder(
                        client,
                        client2,
                        client4,
                        client5
                );
    }

}
