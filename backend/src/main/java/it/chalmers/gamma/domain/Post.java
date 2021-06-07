package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.util.domain.abstraction.DTO;

public record Post(PostId id,
                   @JsonUnwrapped Text name,
                   EmailPrefix emailPrefix
) implements DTO {}

