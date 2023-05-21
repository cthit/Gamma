package it.chalmers.gamma.app.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.security.user.PasswordConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ClientFacade.class,
        ClientRepositoryAdapter.class,
        ClientEntityConverter.class,
        ApiKeyRepositoryAdapter.class,
        ApiKeyEntityConverter.class,
        UserRepositoryAdapter.class,
        PasswordConfiguration.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        ClientAuthorityRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        ClientAuthorityEntityConverter.class,
        SuperGroupEntityConverter.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        PostEntityConverter.class,
        UserRepositoryAdapter.class,
        PasswordConfiguration.class,
        PostRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class,
        SettingsRepositoryAdapter.class})
public class ClientFacadeIntegrationTest {
/*
    @MockBean
    private AccessGuard accessGuard;
    @Autowired
    private ClientFacade clientFacade;
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
    public void Given_ANewValidClient_Expect_create_To_Work() throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
        clientAuthorityRepository.create(new AuthorityName("mat"));

        ClientFacade.NewClient newClient = new ClientFacade.NewClient(
                "https://mat.chalmers.it",
                "Mat",
                "Klient för Mat",
                "Client for mat",
                true,
//                List.of("mat"),
                true
        );

        ClientFacade.ClientAndApiKeySecrets secrets = clientFacade.create(newClient);

        assertThat(secrets)
                .hasNoNullFieldsOrProperties();

        ClientFacade.ClientDTO clientDTO = clientFacade.get(secrets.clientId()).orElseThrow();
        assertThat(clientDTO)
                .isEqualTo(new ClientFacade.ClientDTO(
                        secrets.clientUid(),
                        secrets.clientId(),
                        newClient.redirectUrl(),
                        newClient.prettyName(),
                        newClient.svDescription(),
                        newClient.enDescription(),
//                        List.of("mat"),
                        true
                ));
    }

 */

}
