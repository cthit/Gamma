package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.entity.DTO;

import java.time.Instant;

public record UserActivation(Cid cid,
                             UserActivationToken token,
                             Instant createdAt)
        implements DTO { }
