package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import it.chalmers.gamma.app.repository.AppSettingsRepository;
import it.chalmers.gamma.app.domain.settings.Settings;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AppSettingsRepositoryAdapter implements AppSettingsRepository {

    private final AppSettingsJpaRepository repository;

    public AppSettingsRepositoryAdapter(AppSettingsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasSettings() {
        return this.repository.findTopByOrderByVersionDesc().isPresent();
    }

    @Transactional
    @Override
    public void setSettings(Settings settings) {
        AppSettingsEntity settingsEntity = this.repository.findTopByOrderByVersionDesc()
                .orElse(new AppSettingsEntity());
        settingsEntity.apply(settings);
        this.repository.save(settingsEntity);
    }

    @Override
    public Settings getSettings() {
        AppSettingsEntity appSettingsEntity = repository.findTopByOrderByVersionDesc().orElseThrow();
        return appSettingsEntity.toDomain();
    }
}
