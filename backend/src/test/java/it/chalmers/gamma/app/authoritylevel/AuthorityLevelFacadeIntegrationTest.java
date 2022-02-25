package it.chalmers.gamma.app.authoritylevel;

import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
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

import java.util.UUID;

import static it.chalmers.gamma.utils.DomainUtils.defaultSettings;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@DataJpaTest
@Import({AuthorityLevelFacade.class,
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
        SettingsRepositoryAdapter.class
})
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthorityLevelFacadeIntegrationTest {

    @MockBean
    private AccessGuard accessGuard;
    @Autowired
    private AuthorityLevelFacade authorityLevelFacade;
    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    public void setSettings() {
        this.settingsRepository.setSettings(defaultSettings);
    }

    @Test
    public void Given_ValidAuthorityLevel_Expect_addUserToAuthorityLevel_With_InvalidUser_To_Throw()
            throws AuthorityLevelRepository.AuthorityLevelAlreadyExistsException {
        authorityLevelFacade.create("hello");

        assertThatExceptionOfType(AuthorityLevelFacade.UserNotFoundException.class)
                .isThrownBy(() -> authorityLevelFacade.addUserToAuthorityLevel("hello", UUID.randomUUID()));
    }

}
