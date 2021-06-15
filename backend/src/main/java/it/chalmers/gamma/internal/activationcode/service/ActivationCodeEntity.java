package it.chalmers.gamma.internal.activationcode.service;

import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.domain.ActivationCode;
import it.chalmers.gamma.domain.ActivationCodeToken;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

@Entity
@Table(name = "activation_code")
public class ActivationCodeEntity extends ImmutableEntity<Cid, ActivationCode> {

    @EmbeddedId
    private Cid cid;

    @Column(name = "code")
    private ActivationCodeToken token;

    @Column(name = "created_at")
    private Instant createdAt;

    protected ActivationCodeEntity() { }

    protected ActivationCodeEntity(Cid cid, ActivationCodeToken token) {
        this.createdAt = Instant.now();
        this.cid = cid;
        this.token = token;
    }

    @Override
    protected ActivationCode toDTO() {
        return new ActivationCode(
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
