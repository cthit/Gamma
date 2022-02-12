package it.chalmers.gamma.app.client;

import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.apikey.ApiKeyRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.security.user.PasswordConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static it.chalmers.gamma.DomainUtils.defaultSettings;
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
        AuthorityLevelRepositoryAdapter.class,
        SuperGroupRepositoryAdapter.class,
        SuperGroupEntityConverter.class,
        AuthorityLevelEntityConverter.class,
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

    @MockBean
    private AccessGuard accessGuard;
    @Autowired
    private ClientFacade clientFacade;
    @Autowired
    private AuthorityLevelRepository authorityLevelRepository;
    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    public void setSettings() {
        this.settingsRepository.setSettings(defaultSettings);
    }

    @Test
    public void Given_ANewValidClient_Expect_create_To_Work() throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        authorityLevelRepository.create(new AuthorityLevelName("mat"));

        ClientFacade.NewClient newClient = new ClientFacade.NewClient(
                "https://mat.chalmers.it",
                "Mat",
                "Klient för Mat",
                "Client for mat",
                true,
                List.of("mat"),
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
                        newClient.webServerRedirectUrl(),
                        newClient.prettyName(),
                        newClient.svDescription(),
                        newClient.enDescription(),
                        List.of("mat"),
                        Collections.emptyList(),
                        true
                ));
    }

}
