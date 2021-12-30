package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostEntityConverter {

    private final PostJpaRepository postJpaRepository;

    public PostEntityConverter(PostJpaRepository postJpaRepository) {
        this.postJpaRepository = postJpaRepository;
    }

    public PostEntity toEntity(Post post) {
        Optional<PostEntity> maybePostEntity = this.postJpaRepository.findById(post.id().value());
        PostEntity postEntity;

        if (maybePostEntity.isPresent()) {
            postEntity = maybePostEntity.get();
            postEntity.increaseVersion(post.version());
        } else {
            postEntity = new PostEntity();
        }

        postEntity.id = post.id().value();
        postEntity.emailPrefix = post.emailPrefix().value();

        if (postEntity.postName == null) {
            postEntity.postName = new TextEntity();
        }

        postEntity.postName.apply(post.name());

        return postEntity;
    }

    public Post toDomain(PostEntity postEntity) {
        return new Post(
                new PostId(postEntity.id),
                postEntity.getVersion(),
                postEntity.postName.toDomain(),
                new EmailPrefix(postEntity.emailPrefix)
        );
    }

}
