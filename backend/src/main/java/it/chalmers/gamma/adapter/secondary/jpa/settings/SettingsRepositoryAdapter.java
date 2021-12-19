package it.chalmers.gamma.adapter.secondary.jpa.settings;

import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import it.chalmers.gamma.app.settings.domain.Settings;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SettingsRepositoryAdapter implements SettingsRepository {

    private final SettingsJpaRepository repository;

    public SettingsRepositoryAdapter(SettingsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasSettings() {
        return this.repository.findTopByOrderByVersionDesc().isPresent();
    }

    @Transactional
    @Override
    public void setSettings(Settings settings) {
        SettingsEntity settingsEntity = this.repository.findTopByOrderByVersionDesc()
                .orElse(new SettingsEntity());
        settingsEntity.apply(settings);
        this.repository.save(settingsEntity);
    }

    /**
     * Assumes that there's always one settings entity available.
     */
    @Override
    public Settings getSettings() {
        SettingsEntity settingsEntity = repository.findTopByOrderByVersionDesc()
                .orElseThrow(IllegalStateException::new);
        return settingsEntity.toDomain();
    }
}
