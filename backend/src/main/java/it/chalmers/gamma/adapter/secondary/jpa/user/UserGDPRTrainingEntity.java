package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ituser_gdpr_training")
public class UserGDPRTrainingEntity extends ImmutableEntity<UserId> {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    protected UserGDPRTrainingEntity() {}

    @Override
    protected UserId id() {
        return this.user.id();
    }

}
