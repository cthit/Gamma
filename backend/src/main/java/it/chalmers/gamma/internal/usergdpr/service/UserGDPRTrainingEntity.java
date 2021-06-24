package it.chalmers.gamma.internal.usergdpr.service;

import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.entity.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ituser_gdpr_training")
public class UserGDPRTrainingEntity extends ImmutableEntity<UserId, UserId> {

    @EmbeddedId
    private UserId userId;

    protected UserGDPRTrainingEntity() {}

    protected UserGDPRTrainingEntity(UserId userId) {
        this.userId = userId;
    }

    @Override
    protected UserId id() {
        return userId;
    }

    @Override
    protected UserId toDTO() {
        return this.userId;
    }
}
