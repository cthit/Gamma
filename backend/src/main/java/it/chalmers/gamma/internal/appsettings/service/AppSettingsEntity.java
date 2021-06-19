package it.chalmers.gamma.internal.appsettings.service;

import it.chalmers.gamma.domain.Settings;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "settings")
public class AppSettingsEntity extends MutableEntity<Integer, Settings> {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_updated_user_agreement")
    private Instant lastUpdatedUserAgreement;

    protected AppSettingsEntity() {

    }

    @Override
    protected Integer id() {
        return null;
    }

    @Override
    protected Settings toDTO() {
        return new Settings();
    }

    @Override
    protected void apply(Settings settings) {
        this.lastUpdatedUserAgreement = settings.lastUpdatedUserAgreement();
    }
}
