package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "ituser_gdpr_training")
public class UserGDPRTrainingEntity extends ImmutableEntity<UserId> {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;

    protected UserGDPRTrainingEntity() {}

    @Override
    protected UserId id() {
        return this.user.id();
    }

}
