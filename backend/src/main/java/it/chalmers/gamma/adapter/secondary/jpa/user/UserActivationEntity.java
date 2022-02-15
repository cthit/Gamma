package it.chalmers.gamma.adapter.secondary.jpa.user;

import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.app.user.activation.domain.UserActivation;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;

@Entity
@Table(name = "user_activation")
public class UserActivationEntity extends ImmutableEntity<String> {

    @Id
    @Column(name = "cid")
    private String cid;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    private Instant createdAt;

    protected UserActivationEntity() { }

    protected UserActivationEntity(Cid cid) {
        this.createdAt = Instant.now();
        this.cid = cid.getValue();
    }

    public void setToken(UserActivationToken token) {
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
    public String getId() {
        return this.cid;
    }

    public Cid cid() {
        return Cid.valueOf(this.cid);
    }

}
