package it.chalmers.gamma.internal.activationcode.service;

import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.domain.ActivationCode;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Code;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

@Entity
@Table(name = "activation_code")
public class ActivationCodeEntity extends ImmutableEntity<Cid, ActivationCode> {

    @EmbeddedId
    private Cid cid;

    @Column(name = "code")
    private Code code;

    @Column(name = "created_at")
    private Instant createdAt;

    protected ActivationCodeEntity() { }

    protected ActivationCodeEntity(Cid cid, Code code) {
        this.createdAt = Instant.now();
        this.cid = cid;
        this.code = code;
    }

    @Override
    protected ActivationCode toDTO() {
        return new ActivationCode(
                this.cid,
                this.code,
                this.createdAt
        );
    }

    @Override
    protected Cid id() {
        return this.cid;
    }
}
