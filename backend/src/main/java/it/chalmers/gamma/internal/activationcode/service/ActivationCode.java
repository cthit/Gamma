package it.chalmers.gamma.internal.activationcode.service;

import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;
import it.chalmers.gamma.util.domain.abstraction.MutableEntity;
import it.chalmers.gamma.util.domain.abstraction.Id;

@Entity
@Table(name = "activation_code")
public class ActivationCode extends ImmutableEntity<Cid, ActivationCodeDTO> {

    @EmbeddedId
    private Cid cid;

    @Column(name = "code")
    private Code code;

    @Column(name = "created_at")
    private Instant createdAt;

    protected ActivationCode() { }

    protected ActivationCode(Cid cid, Code code) {
        this.createdAt = Instant.now();
        this.cid = cid;
        this.code = code;
    }

    @Override
    protected ActivationCodeDTO toDTO() {
        return new ActivationCodeDTO(
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
