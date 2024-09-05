package it.chalmers.gamma.app.post.domain;

import io.soabase.recordbuilder.core.RecordBuilder;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import java.util.Objects;

@RecordBuilder
public record Post(PostId id, int version, Text name, EmailPrefix emailPrefix, Order order)
    implements PostBuilder.With {

  public Post {
    Objects.requireNonNull(id);
    Objects.requireNonNull(name);
    Objects.requireNonNull(emailPrefix);
  }
}
