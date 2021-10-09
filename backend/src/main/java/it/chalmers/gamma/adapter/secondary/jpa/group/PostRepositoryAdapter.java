package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.port.repository.PostRepository;
import it.chalmers.gamma.app.domain.post.Post;
import it.chalmers.gamma.app.domain.post.PostId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostRepositoryAdapter implements PostRepository {

    private final PostJpaRepository repository;
    private final PostEntityConverter postEntityConverter;

    public PostRepositoryAdapter(PostJpaRepository repository,
                                 PostEntityConverter postEntityConverter) {
        this.repository = repository;
        this.postEntityConverter = postEntityConverter;
    }

    @Override
    public void save(Post post) {
        this.repository.save(this.postEntityConverter.toEntity(post));
    }

    @Override
    public void delete(PostId postId) throws PostNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Post> getAll() {
        return this.repository.findAll().stream().map(this.postEntityConverter::toDomain).toList();
    }

    @Override
    public Optional<Post> get(PostId postId) {
        return this.repository.findById(postId.value()).map(this.postEntityConverter::toDomain);
    }
}
