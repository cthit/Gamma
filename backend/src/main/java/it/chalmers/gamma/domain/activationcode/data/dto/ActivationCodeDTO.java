package it.chalmers.gamma.domain.activationcode.data.dto;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public class ActivationCodeDTO implements DTO {

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

    public Cid getCid() {
        return this.cid;
    }

    public String getCode() {
        return this.code;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

}
