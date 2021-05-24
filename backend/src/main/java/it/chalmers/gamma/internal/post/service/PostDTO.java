package it.chalmers.gamma.internal.post.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.EmailPrefix;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.internal.text.service.TextDTO;

public record PostDTO(PostId id,
                      @JsonUnwrapped TextDTO name,
                      EmailPrefix emailPrefix
) implements DTO {}

