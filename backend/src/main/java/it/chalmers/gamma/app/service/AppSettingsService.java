package it.chalmers.gamma.app.service;

import it.chalmers.gamma.app.domain.Settings;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AppSettingsService {

    private final AppSettingsRepository repository;

    public AppSettingsService(AppSettingsRepository repository) {
        this.repository = repository;
    }

    public Settings getSettings() {
        return this.repository.findTopByOrderByIdDesc().toDTO();
    }

    public void resetUserAgreement() {
        AppSettingsEntity appSettingsEntity = this.repository.findTopByOrderByIdDesc();
        Settings settings = appSettingsEntity.toDTO();
        Settings newSettings = settings.withLastUpdatedUserAgreement(Instant.now());
        appSettingsEntity.apply(newSettings);
    }

    public boolean hasSettings() {
        return this.repository.count() > 0;
    }

    public void addSettings(Settings settings) {
        this.repository.save(new AppSettingsEntity(settings));
    }
}
