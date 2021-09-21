package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.post.PostRepository;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.post.PostId;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PostRepositoryAdapter implements PostRepository {
    @Override
    public void create(Post post) {

    }

    @Override
    public void save(Post post) throws PostNotFoundException {

    }

    @Override
    public void delete(PostId postId) throws PostNotFoundException {

    }

    @Override
    public List<Post> getAll() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Post> get(PostId postId) {
        return Optional.empty();
    }
}
