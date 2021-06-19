package it.chalmers.gamma.internal.appsettings.service;

import it.chalmers.gamma.domain.Settings;
import it.chalmers.gamma.domain.SettingsId;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "settings")
public class AppSettingsEntity extends MutableEntity<SettingsId, Settings> {

    @EmbeddedId
    private SettingsId id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_updated_user_agreement")
    private Instant lastUpdatedUserAgreement;

    protected AppSettingsEntity() {

    }

    @Override
    protected SettingsId id() {
        return this.id;
    }

    @Override
    protected Settings toDTO() {
        return new Settings(this.lastUpdatedUserAgreement);
    }

    @Override
    protected void apply(Settings settings) {
        this.lastUpdatedUserAgreement = settings.lastUpdatedUserAgreement();
    }
}
