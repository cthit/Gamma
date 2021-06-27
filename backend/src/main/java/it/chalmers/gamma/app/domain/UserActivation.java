package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.time.Instant;

public record UserActivation(Cid cid,
                             UserActivationToken token,
                             Instant createdAt)
        implements DTO { }
