package it.chalmers.gamma.internal.appsettings.service;

import it.chalmers.gamma.domain.Settings;
import org.springframework.stereotype.Service;

@Service
public class AppSettingsService {

    private Settings settings;

    private final AppSettingsRepository repository;

    public AppSettingsService(AppSettingsRepository repository) {
        this.repository = repository;
    }

    public void resetSettings() {
        this.settings = null;
    }

    public Settings getSettings() {
        if (this.settings == null) {
            AppSettingsEntity appSettingsEntity = this.repository.findTopByOrderByIdDesc();
            this.settings = new Settings(appSettingsEntity.lastUpdatedUserAgreement);
        }

        return this.settings;
    }

}
