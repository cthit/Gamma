package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.passwordCheck;

@Service
public class UserAgreementFacade extends Facade {

    private final SettingsRepository settingsRepository;

    public UserAgreementFacade(AccessGuard accessGuard, SettingsRepository settingsRepository) {
        super(accessGuard);
        this.settingsRepository = settingsRepository;
    }

    public void resetUserAgreement(String password) {
        this.accessGuard.requireAll(
                isAdmin(),
                passwordCheck(password)
        );

        this.settingsRepository.setSettings(
                settingsRepository.getSettings().withLastUpdatedUserAgreement(Instant.now())
        );
    }

}
