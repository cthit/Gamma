package it.chalmers.gamma.internal.usergdpr.service;

import it.chalmers.gamma.internal.user.service.UserId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ituser_gdpr_training")
public class UserGDPRTraining {

    @EmbeddedId
    private UserId userId;

    protected UserGDPRTraining() {}

    public UserGDPRTraining(UserId userId) {
        this.userId = userId;
    }

}
