package it.chalmers.gamma.app;

import it.chalmers.gamma.app.domain.Post;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.post.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostFacade extends Facade {

    private final PostRepository postRepository;

    public PostFacade(AccessGuard accessGuard, PostRepository postRepository) {
        super(accessGuard);
        this.postRepository = postRepository;
    }

    public Optional<Post> get(PostId postId) {
        accessGuard.requireSignedIn();
        return this.postRepository.get(postId);
    }

    public List<Post> getAll() {
        accessGuard.requireSignedIn();
        return this.postRepository.getAll();
    }

}
