package it.chalmers.gamma.adapter.secondary.jpa.settings;

import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "g_settings")
public class SettingsEntity extends MutableEntity<SettingsId> {

    @Column(name = "updated_at")
    protected Instant updatedAt;
    @OneToMany(mappedBy = "id.settings", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<SettingsInfoSuperGroupTypeEntity> infoSuperGroupTypeEntities;
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    protected SettingsEntity() {
        this.id = UUID.randomUUID();
        this.infoSuperGroupTypeEntities = new ArrayList<>();
    }

    @Override
    public SettingsId getId() {
        return new SettingsId(this.id);
    }

    public Settings toDomain() {
        return new Settings(
                this.infoSuperGroupTypeEntities
                        .stream()
                        .map(SettingsInfoSuperGroupTypeEntity::getSuperGroupType)
                        .toList()
        );
    }
}
