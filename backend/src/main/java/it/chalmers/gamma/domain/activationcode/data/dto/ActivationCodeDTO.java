package it.chalmers.gamma.domain.activationcode.data.dto;

import it.chalmers.gamma.domain.activationcode.Code;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;
import java.util.Objects;

public class ActivationCodeDTO implements DTO {

    private final Cid cid;
    private final Code code;
    private final Instant createdAt;

    public ActivationCodeDTO(Cid cid,
                             Code code,
                             Instant createdAt) {
        this.cid = cid;
        this.code = code;
        this.createdAt = createdAt;
    }

    public Cid getCid() {
        return this.cid;
    }

    public Code getCode() {
        return this.code;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

}
