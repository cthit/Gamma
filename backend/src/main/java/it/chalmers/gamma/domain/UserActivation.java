package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Instant;

public record UserActivation(Cid cid,
                             UserActivationToken token,
                             Instant createdAt)
        implements DTO { }
