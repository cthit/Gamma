package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.app.domain.UserActivation;
import it.chalmers.gamma.app.domain.UserActivationToken;
import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

@Entity
@Table(name = "user_activation")
public class UserActivationEntity extends ImmutableEntity<Cid> {

    @Id
    @Column(name = "cid")
    private String cid;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    private Instant createdAt;

    protected UserActivationEntity() { }

    protected UserActivationEntity(Cid cid, UserActivationToken token) {
        this.createdAt = Instant.now();
        this.cid = cid.value();
        this.token = token.value();
    }

    public UserActivation toDomain() {
        return new UserActivation(
                Cid.valueOf(this.cid),
                new UserActivationToken(this.token),
                this.createdAt
        );
    }

    @Override
    protected Cid id() {
        return Cid.valueOf(this.cid);
    }
}
