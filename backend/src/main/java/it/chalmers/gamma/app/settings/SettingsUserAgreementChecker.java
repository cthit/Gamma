package it.chalmers.gamma.app.settings;

import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.user.domain.User;
import org.springframework.stereotype.Service;

@Service
public class SettingsUserAgreementChecker {

    private final SettingsRepository settingsRepository;

    public SettingsUserAgreementChecker(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public boolean hasAcceptedLatestUserAgreement(User user) {
        Settings settings = settingsRepository.getSettings();
        return settings.lastUpdatedUserAgreement().compareTo(user.lastAcceptedUserAgreement()) < 0;
    }

}
