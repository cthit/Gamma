package it.chalmers.gamma.adapter.secondary.jpa.settings;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeEntity;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SettingsRepositoryAdapter implements SettingsRepository {

    private static final PersistenceErrorState superGroupTypeNotFound = new PersistenceErrorState(
            null,
            PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION
    );
    private final SettingsJpaRepository repository;

    public SettingsRepositoryAdapter(SettingsJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasSettings() {
        return this.repository.findTopByOrderByVersionDesc().isPresent();
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

    @Override
    public void setSettings(UpdateSettings updateSettings) {
        setSettings(updateSettings.updateSettings(getSettings()));
    }

    @Override
    public void setSettings(Settings settings) {
        try {
            this.repository.saveAndFlush(toEntity(settings));
        } catch (Exception e) {
            PersistenceErrorState state = PersistenceErrorHelper.getState(e);

            if (state.equals(superGroupTypeNotFound)) {
                throw new IllegalArgumentException();
            }

            throw e;
        }
    }

    private SettingsEntity toEntity(Settings settings) {
        SettingsEntity settingsEntity = this.repository.findTopByOrderByVersionDesc()
                .orElse(new SettingsEntity());

        settingsEntity.updatedAt = Instant.now();
        settingsEntity.lastUpdatedUserAgreement = settings.lastUpdatedUserAgreement();
        settingsEntity.infoSuperGroupTypeEntities.clear();
        settingsEntity.infoSuperGroupTypeEntities.addAll(
                settings.infoSuperGroupTypes()
                        .stream()
                        .map(superGroupType -> new SettingsInfoSuperGroupTypeEntity(
                                settingsEntity,
                                new SuperGroupTypeEntity(superGroupType)
                        ))
                        .toList()
        );

        return settingsEntity;
    }

}
