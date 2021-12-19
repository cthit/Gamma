package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.settings.domain.Settings;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SettingsRepositoryAdapter implements SettingsRepository {

    private final AppSettingsJpaRepository repository;

    public SettingsRepositoryAdapter(AppSettingsJpaRepository repository) {
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
