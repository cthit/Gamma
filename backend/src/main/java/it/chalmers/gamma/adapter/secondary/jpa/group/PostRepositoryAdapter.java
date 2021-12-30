package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.util.DataIntegrityErrorState;
import it.chalmers.gamma.adapter.secondary.jpa.util.DataIntegrityViolationHelper;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            this.repository.save(this.postEntityConverter.toEntity(post));
        } catch (DataIntegrityViolationException e) {
            DataIntegrityErrorState state = DataIntegrityViolationHelper.getState(e);

            System.out.println(state);

            throw e;
        }
    }

    @Override
    public void delete(PostId postId) throws PostNotFoundException {
        this.repository.deleteById(postId.value());
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
