package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.post.PostRepository;
import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.post.PostId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostRepositoryAdapter implements PostRepository {

    private final PostJpaRepository repository;

    public PostRepositoryAdapter(PostJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(Post post) {
        this.repository.save(new PostEntity(post));
    }

    @Override
    public void save(Post post) throws PostNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(PostId postId) throws PostNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Post> getAll() {
        return this.repository.findAll().stream().map(PostEntity::toDomain).toList();
    }

    @Override
    public Optional<Post> get(PostId postId) {
        return this.repository.findById(postId.value()).map(PostEntity::toDomain);
    }
}
