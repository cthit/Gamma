package it.chalmers.gamma.app.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record Post(PostId id,
                   @JsonUnwrapped Text name,
                   EmailPrefix emailPrefix
) implements DTO {}

