package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import javax.validation.constraints.NotNull;

public record Text(@NotNull String sv,
                   @NotNull String en)
        implements DTO { }