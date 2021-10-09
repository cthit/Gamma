package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.domain.post.Post;
import org.springframework.stereotype.Service;

@Service
public class PostEntityConverter {

    private final PostJpaRepository postJpaRepository;

    public PostEntityConverter(PostJpaRepository postJpaRepository) {
        this.postJpaRepository = postJpaRepository;
    }

    public PostEntity toEntity(Post post) {
        throw new UnsupportedOperationException();
    }

    public Post toDomain(PostEntity postEntity) {
        throw new UnsupportedOperationException();
    }

}
