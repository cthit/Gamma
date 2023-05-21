package it.chalmers.gamma.app.authority;

import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.client.authority.ClientAuthorityRepositoryAdapter;
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

@ActiveProfiles("test")
@DataJpaTest
@Import({ClientAuthorityFacade.class,
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
        SettingsRepositoryAdapter.class
})
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClientAuthorityFacadeIntegrationTest {
/*
    @MockBean
    private AccessGuard accessGuard;
    @Autowired
    private ClientAuthorityFacade clientAuthorityFacade;
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
    public void Given_ValidAuthorityLevel_Expect_addUserToAuthorityLevel_With_InvalidUser_To_Throw()
            throws ClientAuthorityRepository.AuthorityLevelAlreadyExistsException {
        clientAuthorityFacade.create("hello");

        assertThatExceptionOfType(ClientAuthorityFacade.UserNotFoundException.class)
                .isThrownBy(() -> clientAuthorityFacade.addUserToAuthorityLevel("hello", UUID.randomUUID()));
    }
*/
}
