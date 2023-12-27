package it.chalmers.gamma.app.settings;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.passwordCheck;

@Service
public class SettingsFacade extends Facade {

    private final SettingsRepository settingsRepository;

    public SettingsFacade(AccessGuard accessGuard, SettingsRepository settingsRepository) {
        super(accessGuard);
        this.settingsRepository = settingsRepository;
    }

    public void resetUserAgreement(String password) {
        this.accessGuard.requireAll(
                isAdmin(),
                passwordCheck(password)
        );

        this.settingsRepository.setSettings(
                settings -> settings.withLastUpdatedUserAgreement(Instant.now())
        );
    }

    @Transactional
    public void setInfoSuperGroupTypes(List<String> superGroupTypes) {
        this.accessGuard.require(isAdmin());

        this.settingsRepository.setSettings(
                settings -> settings.withInfoSuperGroupTypes(
                        superGroupTypes
                                .stream()
                                .map(SuperGroupType::new)
                                .toList()
                )
        );
    }

    public List<String> getInfoApiSuperGroupTypes() {
        this.accessGuard.require(isAdmin());

        return this.settingsRepository.getSettings()
                .infoSuperGroupTypes()
                .stream()
                .map(SuperGroupType::value)
                .toList();
    }
}