package it.chalmers.gamma.app.domain;

import java.util.Objects;

public record Post(PostId id,
                   Text name,
                   EmailPrefix emailPrefix
) {

    public Post {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(emailPrefix);
    }

}

