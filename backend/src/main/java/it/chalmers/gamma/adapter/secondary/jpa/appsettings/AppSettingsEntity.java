package it.chalmers.gamma.adapter.secondary.jpa.appsettings;

import it.chalmers.gamma.app.domain.Settings;
import it.chalmers.gamma.app.domain.SettingsId;
import it.chalmers.gamma.adapter.secondary.jpa.util.MutableEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "settings")
public class AppSettingsEntity extends MutableEntity<SettingsId> {

    @EmbeddedId
    private SettingsId id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_updated_user_agreement")
    private Instant lastUpdatedUserAgreement;

    protected AppSettingsEntity() {

    }

    protected AppSettingsEntity(Settings settings) {
        this.id = SettingsId.generate();
        this.createdAt = Instant.now();
        this.apply(settings);
    }

    @Override
    protected SettingsId id() {
        return this.id;
    }

    public Settings toDomain() {
        return new Settings(this.lastUpdatedUserAgreement);
    }

    protected void apply(Settings settings) {
        this.lastUpdatedUserAgreement = settings.lastUpdatedUserAgreement();
    }
}
