package it.chalmers.gamma.domain.usergdpr.data;

import it.chalmers.gamma.domain.user.UserId;

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
