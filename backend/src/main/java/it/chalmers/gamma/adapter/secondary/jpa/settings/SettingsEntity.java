package it.chalmers.gamma.adapter.secondary.jpa.settings;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupTypeEntity;
import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "settings")
public class SettingsEntity extends MutableEntity<SettingsId> {

    @Id
    private UUID id;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "last_updated_user_agreement")
    private Instant lastUpdatedUserAgreement;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "id.settings", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<SettingsInfoSuperGroupTypeEntity> infoSuperGroupTypeEntities;

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
                this.lastUpdatedUserAgreement,
                this.infoSuperGroupTypeEntities
                        .stream()
                        .map(SettingsInfoSuperGroupTypeEntity::getSuperGroupType)
                        .toList()
        );
    }

    protected void apply(Settings settings) {
        this.updatedAt = Instant.now();
        this.lastUpdatedUserAgreement = settings.lastUpdatedUserAgreement();
        this.infoSuperGroupTypeEntities.clear();
        this.infoSuperGroupTypeEntities.addAll(
                settings.infoSuperGroupTypes()
                        .stream()
                        .map(superGroupType -> new SettingsInfoSuperGroupTypeEntity(
                                this,
                                new SuperGroupTypeEntity(superGroupType)
                        ))
                        .toList()
        );
    }
}
