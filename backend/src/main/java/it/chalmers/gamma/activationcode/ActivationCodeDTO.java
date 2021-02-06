package it.chalmers.gamma.activationcode;

import it.chalmers.gamma.domain.Cid;

import java.time.Instant;

public class ActivationCodeDTO {

    private final Cid cid;
    private final String code;
    private final Instant createdAt;

    public ActivationCodeDTO(Cid cid,
                             String code,
                             Instant createdAt) {
        this.cid = cid;
        this.code = code;
        this.createdAt = createdAt;
    }

    public String getCid() {
        return this.cid.value;
    }

    public String getCode() {
        return this.code;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

}
