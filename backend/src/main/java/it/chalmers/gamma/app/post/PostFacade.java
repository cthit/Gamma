package it.chalmers.gamma.app.post;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.post.PostId;
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

    public void create(Post post) {
        this.postRepository.create(post);
    }

    public void update(Post post) throws PostRepository.PostNotFoundException {
        this.postRepository.save(post);
    }

    public void delete(PostId postId) throws PostRepository.PostNotFoundException {
        this.postRepository.delete(postId);
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
