package it.chalmers.gamma.domain.post.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

public record PostDTO(PostId id,
                      @JsonUnwrapped TextDTO name,
                      EmailPrefix emailPrefix
) implements DTO {}

