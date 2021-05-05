package it.chalmers.gamma.domain.text.data.dto;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import javax.validation.constraints.NotNull;

public record TextDTO(@NotNull String sv,
                      @NotNull String en)
        implements DTO { }