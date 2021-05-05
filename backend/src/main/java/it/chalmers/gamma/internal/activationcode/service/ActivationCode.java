package it.chalmers.gamma.internal.activationcode.service;

import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.BaseEntity;

@Entity
@Table(name = "activation_code")
public class ActivationCode extends BaseEntity<ActivationCodeDTO> {

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
}
