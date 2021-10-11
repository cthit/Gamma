package it.chalmers.gamma.app.usecase;

import it.chalmers.gamma.app.domain.settings.Settings;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.repository.AppSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class UserAgreementCheckUseCase {

    private final AppSettingsRepository appSettingsRepository;

    public UserAgreementCheckUseCase(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    public boolean hasAcceptedLatestUserAgreement(User user) {
        Settings settings = appSettingsRepository.getSettings();
        return settings.lastUpdatedUserAgreement().compareTo(user.lastAcceptedUserAgreement()) < 0;
    }

}
