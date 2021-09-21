package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import it.chalmers.gamma.domain.Settings;
import it.chalmers.gamma.domain.SettingsId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settings")
public class AppSettingsEntity extends MutableEntity<SettingsId> {

    @Id
    private UUID id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_updated_user_agreement")
    private Instant lastUpdatedUserAgreement;

    protected AppSettingsEntity() {

    }

    protected AppSettingsEntity(Settings settings) {
        this.id = SettingsId.generate().getValue();
        this.createdAt = Instant.now();
        this.apply(settings);
    }

    @Override
    protected SettingsId id() {
        return new SettingsId(this.id);
    }

    public Settings toDomain() {
        return new Settings(this.lastUpdatedUserAgreement);
    }

    protected void apply(Settings settings) {
        this.lastUpdatedUserAgreement = settings.lastUpdatedUserAgreement();
    }
}
