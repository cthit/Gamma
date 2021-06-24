package it.chalmers.gamma.internal.useractivation.service;

import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.domain.UserActivation;
import it.chalmers.gamma.domain.UserActivationToken;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.util.entity.ImmutableEntity;

@Entity
@Table(name = "user_activation")
public class UserActivationEntity extends ImmutableEntity<Cid, UserActivation> {

    @EmbeddedId
    private Cid cid;

    @Embedded
    private UserActivationToken token;

    @Column(name = "created_at")
    private Instant createdAt;

    protected UserActivationEntity() { }

    protected UserActivationEntity(Cid cid, UserActivationToken token) {
        this.createdAt = Instant.now();
        this.cid = cid;
        this.token = token;
    }

    @Override
    protected UserActivation toDTO() {
        return new UserActivation(
                this.cid,
                this.token,
                this.createdAt
        );
    }

    @Override
    protected Cid id() {
        return this.cid;
    }
}
