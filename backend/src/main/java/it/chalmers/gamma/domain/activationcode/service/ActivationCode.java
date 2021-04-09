package it.chalmers.gamma.domain.activationcode.service;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.BaseEntity;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "activation_code")
public class ActivationCode extends BaseEntity<ActivationCodeDTO> {

    @EmbeddedId
    private Cid cid;

    @Column(name = "code")
    private Code code;

    @Column(name = "created_at")
    private Instant createdAt;

    @Transient
    @Value("${password-expiration-time}")
    private static final int PASSWORD_EXPIRATION_TIME = 3600;

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

    public boolean isValid() {
        return Instant.now().isBefore(this.createdAt.plus(Duration.ofSeconds(PASSWORD_EXPIRATION_TIME)));
    }
}
