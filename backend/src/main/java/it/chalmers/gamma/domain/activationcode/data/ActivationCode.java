package it.chalmers.gamma.domain.activationcode.data;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.*;

import it.chalmers.gamma.domain.Cid;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "activation_code")
public class ActivationCode {

    @EmbeddedId
    private Cid cid;

    @Column(name = "code")
    private String code;

    @Column(name = "created_at")
    private Instant createdAt;

    @Transient
    @Value("${password-expiration-time}")
    private static final int PASSWORD_EXPIRATION_TIME = 3600;

    protected ActivationCode() { }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ActivationCode(Cid cid, String code) {
        this.createdAt = Instant.now();
        this.cid = cid;
        this.code = code;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Cid getCid() {
        return this.cid;
    }

    public void setCid(Cid cid) {
        this.cid = cid;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isValid() {
        return Instant.now().isBefore(this.createdAt.plus(Duration.ofSeconds(PASSWORD_EXPIRATION_TIME)));
    }
}
