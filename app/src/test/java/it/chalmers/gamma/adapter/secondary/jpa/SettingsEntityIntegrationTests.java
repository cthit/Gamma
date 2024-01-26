package it.chalmers.gamma.adapter.secondary.jpa;

import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.settings.SettingsRepositoryAdapter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeRepositoryAdapter;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static it.chalmers.gamma.utils.DomainUtils.board;
import static it.chalmers.gamma.utils.DomainUtils.committee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@Import({SettingsRepositoryAdapter.class,
        SuperGroupTypeRepositoryAdapter.class})
public class SettingsEntityIntegrationTests extends AbstractEntityIntegrationTests {

    @Autowired
    private SettingsRepositoryAdapter settingsRepositoryAdapter;

    @Autowired
    private SettingsJpaRepository settingsJpaRepository;

    @Autowired
    private SuperGroupTypeRepository superGroupTypeRepository;

    @BeforeEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void Given_NoSettings_Expect_getSettings_To_Throw() {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> settingsRepositoryAdapter.getSettings());
    }

    @Test
    public void Given_Settings_Expect_hasSettings_To_Work() {
        Settings settings = new Settings(
                Collections.emptyList()
        );

        settingsRepositoryAdapter.setSettings(settings);

        assertThat(settingsRepositoryAdapter.hasSettings())
                .isTrue();
    }

    @Test
    public void Given_InvalidSuperGroupTypes_Expect_setSettings_To_Throw() {
        Settings validSettings = new Settings(
                Collections.emptyList()
        );

        this.settingsRepositoryAdapter.setSettings(validSettings);

        Settings settings = new Settings(
                List.of(committee, board)
        );

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> settingsRepositoryAdapter.setSettings(settings));
    }

    @Test
    public void Given_Settings_Expect_setSettingsWithOnlySuperGroupTypes_To_NotUpdateLastUpdatedUserAgreement() throws SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException {
        superGroupTypeRepository.add(committee);
        superGroupTypeRepository.add(board);

        Settings settings = new Settings(
                List.of(committee)
        );

        settingsRepositoryAdapter.setSettings(settings);

        assertThat(settingsRepositoryAdapter.getSettings())
                .isEqualTo(settings);

        settingsRepositoryAdapter.setSettings(oldSettings -> oldSettings.withInfoSuperGroupTypes(List.of(board, committee)));

        assertThat(settingsJpaRepository.findAll())
                .hasSize(1);

        assertThat(settingsRepositoryAdapter.getSettings()).isEqualTo(new Settings(
                List.of(board, committee)
        ));
    }

}
