package it.chalmers.gamma.app.domain.post;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.domain.group.EmailPrefix;
import it.chalmers.gamma.app.domain.common.Text;

import java.util.Objects;

@RecordBuilder
public record Post(PostId id,
                   Text name,
                   EmailPrefix emailPrefix
) {

    public Post {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(emailPrefix);
    }

    public static Post create(Text name, EmailPrefix emailPrefix) {
        return new Post(PostId.generate(), name, emailPrefix);
    }

    public Post update(Text name, EmailPrefix emailPrefix) {
        return new Post(id, name, emailPrefix);
    }
}

