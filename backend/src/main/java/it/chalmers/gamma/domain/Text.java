package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

import javax.validation.constraints.NotNull;

public record Text(TextValue sv,
                   TextValue en)
        implements DTO {

    public Text(String sv, String en) {
        this(TextValue.valueOf(sv), TextValue.valueOf(en));
    }

    public Text() {
        this(TextValue.empty(), TextValue.empty());
    }
}