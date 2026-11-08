package it.chalmers.gamma.app.client.domain.authority;

import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import java.util.Objects;

/** The members of the super group's groups who hold the post. */
public record AuthoritySuperGroupPost(SuperGroup superGroup, Post post) {

  public AuthoritySuperGroupPost {
    Objects.requireNonNull(superGroup);
    Objects.requireNonNull(post);
  }
}
