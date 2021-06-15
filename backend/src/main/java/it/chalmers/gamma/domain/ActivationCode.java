package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public record ActivationCode(Cid cid,
                             ActivationCodeToken token,
                             Instant createdAt)
        implements DTO { }
