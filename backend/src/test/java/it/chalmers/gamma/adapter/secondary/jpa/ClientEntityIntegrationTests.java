package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.security.user.PasswordConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedAsAdminUser;
import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedAsClientWithApi;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Import({ClientRepositoryAdapter.class,
        ClientEntityConverter.class,
        UserRepositoryAdapter.class,
        PasswordConfiguration.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        ApiKeyRepositoryAdapter.class,
        ApiKeyEntityConverter.class,
        ClientAuthorityRepositoryAdapter.class,
        ClientAuthorityEntityConverter.class,
        SuperGroupEntityConverter.class,
        PostEntityConverter.class,
        UserEntityConverter.class,
        SettingsRepositoryAdapter.class})
public class ClientEntityIntegrationTests extends AbstractEntityIntegrationTests {

    /*
    @Autowired
    private ClientRepositoryAdapter clientRepositoryAdapter;
    @Autowired
    private ApiKeyRepository apiKeyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientAuthorityRepository clientAuthorityRepository;
    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


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
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                null,
                new ClientOwnerOfficial()
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
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                List.of(Scope.PROFILE, Scope.EMAIL),
                null,
                new ClientOwnerOfficial()
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
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                apiKey,
                new ClientOwnerOfficial()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithRestrictions_Expect_save_To_Work() throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
        clientAuthorityRepository.create(new AuthorityName("admin"));
        setAuthenticatedAsAdminUser(userRepository, null);

        addAll(userRepository, u1);
        clientAuthorityRepository.save(new Authority(
                new AuthorityName("admin"),
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(removeUserExtended(u1))
        ));

        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
//                List.of(new AuthorityName("admin")),
                Collections.emptyList(),
                null,
                new ClientOwnerOfficial()
        );

        this.clientRepositoryAdapter.save(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);
    }

    @Test
    public void Given_AValidClientWithApprovedUsers_Expect_save_To_Work() {
        GammaUser user = setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);

        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                new ApiKey(
                        ApiKeyId.generate(),
                        new PrettyName("Api Key"),
                        new Text(),
                        ApiKeyType.CLIENT,
                        ApiKeyToken.generate()
                ),
                new ClientOwnerOfficial()
        );

        this.clientRepositoryAdapter.save(newClient);

        setAuthenticatedAsClientWithApi(newClient);

        Client savedClient = this.clientRepositoryAdapter.get(uid).orElseThrow();

        assertThat(savedClient)
                .isEqualTo(newClient);

        setAuthenticatedAsAdminUser(user);

        addAll(userRepository, u1, u2, u4);

        throw new UnsupportedOperationException();
//        Client newClient2 = newClient.withApprovedUsers(
//                List.of(u1, u2, u4)
//        );
//
//        this.clientRepositoryAdapter.save(newClient2);
//
//        setAuthenticatedAsClientWithApi(newClient);
//
//        Client savedClient2 = this.clientRepositoryAdapter.get(uid).orElseThrow();
//
//        //No u2 since they are locked
//        assertThat(savedClient2)
//                .isEqualTo(removeUserExtended(newClient2.withApprovedUsers(List.of(u1, u4))));
    }

    @Test
    public void Given_AValidClient_Expect_save_And_delete_To_Work() throws ClientRepository.ClientNotFoundException {
        ClientUid uid = ClientUid.generate();
        Client newClient = new Client(
                uid,
                ClientId.generate(),
                ClientSecret.generate(),
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                null,
                new ClientOwnerOfficial()
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
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                apiKey,
                new ClientOwnerOfficial()
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
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                null,
                new ClientOwnerOfficial()
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
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                apiKey,
                new ClientOwnerOfficial()
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
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
//                List.of(new AuthorityName("test")),
                Collections.emptyList(),
                null,
                new ClientOwnerOfficial()
        );

        assertThatExceptionOfType(ClientRepository.AuthorityLevelNotFoundRuntimeException.class)
                .isThrownBy(() -> this.clientRepositoryAdapter.save(newClient));
    }

    @Test
    public void Given_SameClientId_Expect_save_To_Work() {
        Client client = new Client(
                ClientUid.generate(),
                ClientId.generate(),
                ClientSecret.generate(),
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
                null,
                new ClientOwnerOfficial()
        );

        this.clientRepositoryAdapter.save(client);

        Client clientWithSameId = new Client(
                ClientUid.generate(),
                client.clientId(),
                client.clientSecret(),
                client.clientRedirectUrl(),
                client.prettyName(),
                client.description(),
                client.scopes(),
                client.clientApiKey().orElse(null),
                client.access()
        );

        assertThatExceptionOfType(ClientRepository.ClientIdAlreadyExistsRuntimeException.class)
                .isThrownBy(() -> this.clientRepositoryAdapter.save(clientWithSameId));
    }

    @Test
    public void Given_UserThatHasApprovedClients_Expect_getClientsByUserApproved_To_Work() {
        setAuthenticatedAsAdminUser(userRepository, clientAuthorityRepository);

        addAll(userRepository, u1);

        Client client = new Client(
                ClientUid.generate(),
                ClientId.generate(),
                ClientSecret.generate(),
                new ClientRedirectUrl("https://mat.chalmers.it"),
                new PrettyName("Mat"),
                new Text(
                        "Klient för mat",
                        "Client for mat"
                ),
                Collections.emptyList(),
//                List.of(asSaved(u1)),
                null,
                new ClientOwnerOfficial()
        );

        //TODO: Fix

//        Client client2 = client.with()
//                .clientId(ClientId.generate())
//                .clientUid(ClientUid.generate())
//                .build();
//        Client client3 = client.with()
//                .clientId(ClientId.generate())
//                .clientUid(ClientUid.generate())
////                .approvedUsers(Collections.emptyList())
//                .build();
//        Client client4 = client.with()
//                .clientId(ClientId.generate())
//                .clientUid(ClientUid.generate())
//                .build();
//        Client client5 = client.with()
//                .clientId(ClientId.generate())
//                .clientUid(ClientUid.generate())
//                .build();
//
//        this.clientRepositoryAdapter.save(client);
//        this.clientRepositoryAdapter.save(client2);
//        this.clientRepositoryAdapter.save(client3);
//        this.clientRepositoryAdapter.save(client4);
//        this.clientRepositoryAdapter.save(client5);
//
//        assertThat(this.clientRepositoryAdapter.getClientsByUserApproved(u1.id()))
//                .containsExactlyInAnyOrder(
//                        client,
//                        client2,
//                        client4,
//                        client5
//                );
    }

     */
}
