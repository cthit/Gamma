package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserPasswordRetrieverAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.security.user.UserPasswordRetriever;
import it.chalmers.gamma.utils.PasswordEncoderTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@Import({UserPasswordRetrieverAdapter.class,
        UserRepositoryAdapter.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        SettingsRepositoryAdapter.class,
        PasswordEncoderTestConfiguration.class})
public class UserPasswordRetrieverEntityIntegrationTests extends AbstractEntityIntegrationTests {

    @Autowired
    private UserPasswordRetrieverAdapter userPasswordRetrieverAdapter;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void Given_ValidUser_Expect_getPassword_To_Work() throws UserRepository.CidAlreadyInUseException, UserRepository.EmailAlreadyInUseException {
        settingsRepository.setSettings(new Settings(
                Instant.now(),
                Collections.emptyList()
        ));

        UserId userId = UserId.generate();
        this.userRepository.create(
                new GammaUser(
                        userId,
                        new Cid("asdf"),
                        new Nick("RandoM"),
                        new FirstName("Smurf"),
                        new LastName("Smurfsson"),
                        new AcceptanceYear(2018),
                        Language.EN,
                        new UserExtended(
                                new Email("smurf@chalmers.it"),
                                0,
                                true,
                                false,
                                ImageUri.defaultUserAvatar(),
                                false
                        )
                ),
                new UnencryptedPassword("password")
        );

        Password password = this.userPasswordRetrieverAdapter.getPassword(userId);

        assertThat(passwordEncoder.matches("password", password.value()))
                .isTrue();
    }

    @Test
    public void Given_InvalidUser_Expect_getPassword_To_Throw() {
        settingsRepository.setSettings(new Settings(
                Instant.now(),
                Collections.emptyList()
        ));

        UserId userId = UserId.generate();

        assertThatExceptionOfType(UserPasswordRetriever.UserNotFoundException.class)
                .isThrownBy(() -> this.userPasswordRetrieverAdapter.getPassword(userId));
    }

}
