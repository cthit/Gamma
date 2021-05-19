package it.chalmers.gamma.internal.user.gdpr.service;

import it.chalmers.gamma.internal.user.service.UserId;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ituser_gdpr_training")
public class UserGDPRTraining extends ImmutableEntity<UserId, UserId> {

    @EmbeddedId
    private UserId userId;

    protected UserGDPRTraining() {}

    public UserGDPRTraining(UserId userId) {
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
