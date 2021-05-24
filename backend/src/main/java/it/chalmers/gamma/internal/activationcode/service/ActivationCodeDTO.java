package it.chalmers.gamma.internal.activationcode.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Code;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public record ActivationCodeDTO(Cid cid,
                         Code code,
                         Instant createdAt)
        implements DTO { }
