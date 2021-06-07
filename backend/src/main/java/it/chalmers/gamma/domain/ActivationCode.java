package it.chalmers.gamma.domain;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Code;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public record ActivationCode(Cid cid,
                             Code code,
                             Instant createdAt)
        implements DTO { }
