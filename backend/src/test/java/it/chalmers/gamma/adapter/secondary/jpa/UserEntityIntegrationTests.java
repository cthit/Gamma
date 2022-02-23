package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.GammaApplication;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthorityLevelRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.group.PostEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.authentication.AuthenticatedService;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils;
import it.chalmers.gamma.utils.PasswordEncoderTestConfiguration;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserRepositoryAdapter;
import it.chalmers.gamma.app.authentication.UserAccessGuard;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Collections;

import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.DEFAULT_USER;
import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedAsNormalUser;
import static it.chalmers.gamma.utils.GammaSecurityContextHolderTestUtils.setAuthenticatedUser;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepositoryAdapter.class,
        UserEntityConverter.class,
        UserAccessGuard.class,
        AuthorityLevelRepositoryAdapter.class,
        AuthorityLevelEntityConverter.class,
        SuperGroupEntityConverter.class,
        PostEntityConverter.class,
        SettingsRepositoryAdapter.class,
        PasswordEncoderTestConfiguration.class})
public class UserEntityIntegrationTests {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;
    @Autowired
    private AuthorityLevelRepositoryAdapter authorityLevelRepositoryAdapter;

    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    public void setupSettings() {
        this.settingsRepository.setSettings(new Settings(
                Instant.ofEpochSecond(0),
                Collections.emptyList()
        ));
    }

    @Test
    public void Given_ValidNewUser_Expect_create_To_Work() {
        User user = setAuthenticatedAsNormalUser(userRepositoryAdapter);

        assertThat(this.userRepositoryAdapter.get(user.id()))
                .get()
                .isEqualTo(user.withExtended(user.extended().withVersion(1)));
    }

    @Test
    public void Given_UserWithoutExtended_Expect_create_To_Throw() {
        User user = DEFAULT_USER.withExtended(null);

        assertThatIllegalStateException()
                .isThrownBy(() -> this.userRepositoryAdapter.create(
                        user,
                        new UnencryptedPassword("password")
                ));
    }

    @Test
    public void Given_ValidUser_Expect_save_To_Work() {
        //Will save the user
        User user = setAuthenticatedAsNormalUser(userRepositoryAdapter);

        User newUser = user.with()
                .nick(new Nick("Smurf_RandoM"))
                .acceptanceYear(new AcceptanceYear(2020))
                .build();

        this.userRepositoryAdapter.save(newUser);

        assertThat(this.userRepositoryAdapter.get(DEFAULT_USER.id()))
                .get()
                .isEqualTo(newUser.withExtended(newUser.extended().withVersion(2)));
    }

    @Test
    public void Given_UserWithNewUserId_Expect_save_To_Throw() {
        User user = setAuthenticatedAsNormalUser(userRepositoryAdapter);
        this.userRepositoryAdapter.create(user, new UnencryptedPassword("password"));
        User loadedUser = this.userRepositoryAdapter.get(user.id()).orElseThrow();
        assertThatExceptionOfType(MutableEntity.IllegalEntityStateException.class)
                .isThrownBy(() -> this.userRepositoryAdapter.save(loadedUser.withId(UserId.generate())));
    }

    @Test
    public void Given_ValidUser_Expect_get_To_HaveAUpdatedVersion() {
        UserId userId = setAuthenticatedAsNormalUser(userRepositoryAdapter).id();
        User loadedUser = this.userRepositoryAdapter.get(userId).orElseThrow();
        assertThat(loadedUser.extended().version())
                .isEqualTo(1);
    }

    @Test
    public void Given_UserWithoutExtended_Expect_save_To_Throw() {
        User user = DEFAULT_USER.withExtended(null);

        assertThatIllegalStateException()
                .isThrownBy(() -> this.userRepositoryAdapter.create(
                        user,
                        new UnencryptedPassword("password")
                ));
    }

    @Test
    public void Given_User_Expect_setPassword_and_checkPassword_To_Work() {
        User user = DEFAULT_USER;
        this.userRepositoryAdapter.create(user, new UnencryptedPassword("password"));
        assertThat(this.userRepositoryAdapter.checkPassword(user.id(), new UnencryptedPassword("password")))
                .isTrue();

        this.userRepositoryAdapter.setPassword(user.id(), new UnencryptedPassword("new_password"));
        assertThat(this.userRepositoryAdapter.checkPassword(user.id(), new UnencryptedPassword("new_password")))
                .isTrue();
    }

    @Test
    public void Given_User_Expect_setPassword_and_wrong_checkPassword_To_Return_False() {
        User user = DEFAULT_USER;
        this.userRepositoryAdapter.create(user, new UnencryptedPassword("password"));
        assertThat(this.userRepositoryAdapter.checkPassword(user.id(), new UnencryptedPassword("_password")))
                .isFalse();

        this.userRepositoryAdapter.setPassword(user.id(), new UnencryptedPassword("new_password"));
        assertThat(this.userRepositoryAdapter.checkPassword(user.id(), new UnencryptedPassword("new_password_")))
                .isFalse();
    }

    @Test
    public void Given_ValidUser_Expect_acceptUserAgreement_To_Work() {
        User user = DEFAULT_USER.withExtended(DEFAULT_USER.extended().withAcceptedUserAgreement(false));

        setAuthenticatedUser(
                userRepositoryAdapter,
                authorityLevelRepositoryAdapter,
                user,
                false
        );

        User updatedUser = this.userRepositoryAdapter.get(user.id()).orElseThrow();
        assertThat(updatedUser.extended().acceptedUserAgreement())
                .isFalse();

        this.userRepositoryAdapter.acceptUserAgreement(user.id());
        updatedUser = this.userRepositoryAdapter.get(user.id()).orElseThrow();
        assertThat(updatedUser.extended().acceptedUserAgreement())
                .isTrue();
    }

}
