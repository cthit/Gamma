package it.chalmers.gamma.internal.activationcode.service;

import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.domain.ActivationCodeDTO;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Code;
import it.chalmers.gamma.util.domain.abstraction.ImmutableEntity;

@Entity
@Table(name = "activation_code")
public class ActivationCodeEntity extends ImmutableEntity<Cid, ActivationCodeDTO> {

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
