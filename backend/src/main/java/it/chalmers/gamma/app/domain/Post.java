package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

import java.util.Objects;

public record Post(PostId id,
                   Text name,
                   EmailPrefix emailPrefix
) implements DTO {

    public Post {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(emailPrefix);
    }

}

