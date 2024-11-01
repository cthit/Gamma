package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.post.domain.Order;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import org.springframework.stereotype.Service;

@Service
public class PostEntityConverter {

  public Post toDomain(PostEntity postEntity) {
    return new Post(
        new PostId(postEntity.id),
        postEntity.getVersion(),
        postEntity.postName.toDomain(),
        new EmailPrefix(postEntity.emailPrefix),
        new Order(postEntity.order));
  }
}
