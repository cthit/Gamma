package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import it.chalmers.gamma.app.settings.AppSettingsRepository;
import it.chalmers.gamma.domain.Settings;
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
        this.repository.save(new AppSettingsEntity(settings));
    }
}
