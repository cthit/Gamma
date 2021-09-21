package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.UUID;


@Entity
@Table(name = "ituser_account_locked")
public class UserLockedEntity extends ImmutableEntity<UserId> {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    private UserEntity user;

    protected UserLockedEntity() {

    }

    @Override
    protected UserId id() {
        return this.user.id();
    }

}
