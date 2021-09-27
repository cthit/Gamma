package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import it.chalmers.gamma.app.port.repository.AppSettingsRepository;
import it.chalmers.gamma.app.domain.settings.Settings;
import org.springframework.stereotype.Service;

@Service
public class AppSettingsRepositoryAdapter implements AppSettingsRepository {

    private final AppSettingsJpaRepository repository;

    public AppSettingsRepositoryAdapter(AppSettingsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasSettings() {
        return this.repository.findTopByOrderByIdDesc().isPresent();
    }

    @Override
    public void setSettings(Settings settings) {
        this.repository.saveAndFlush(new AppSettingsEntity(settings));
    }
}
