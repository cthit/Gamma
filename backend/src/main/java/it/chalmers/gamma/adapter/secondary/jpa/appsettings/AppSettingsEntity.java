package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import it.chalmers.gamma.app.settings.domain.Settings;
import it.chalmers.gamma.app.settings.domain.SettingsId;
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

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "last_updated_user_agreement")
    private Instant lastUpdatedUserAgreement;

    protected AppSettingsEntity() {
        this.id = UUID.randomUUID();
    }

    @Override
    protected SettingsId domainId() {
        return new SettingsId(this.id);
    }

    public Settings toDomain() {
        return new Settings(this.lastUpdatedUserAgreement);
    }

    protected void apply(Settings settings) {
        this.updatedAt = Instant.now();
        this.lastUpdatedUserAgreement = settings.lastUpdatedUserAgreement();
    }
}
