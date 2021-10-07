package it.chalmers.gamma.app.domain.post;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.domain.group.EmailPrefix;
import it.chalmers.gamma.app.domain.common.Text;

import java.util.Objects;

@RecordBuilder
public record Post(PostId id,
                   int version,
                   Text name,
                   EmailPrefix emailPrefix
) implements PostBuilder.With {

    public Post {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(emailPrefix);
    }

    public static Post create(Text name, EmailPrefix emailPrefix) {
        return new Post(PostId.generate(), 0, name, emailPrefix);
    }

}

