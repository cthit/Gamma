package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PostRepositoryAdapter implements PostRepository {

  private final PostJpaRepository repository;
  private final PostEntityConverter postEntityConverter;

  public PostRepositoryAdapter(
      PostJpaRepository repository, PostEntityConverter postEntityConverter) {
    this.repository = repository;
    this.postEntityConverter = postEntityConverter;
  }

  @Override
  public void save(Post post) {
    this.repository.saveAndFlush(toEntity(post));
  }

  @Override
  public void saveAll(List<Post> posts) {
    this.repository.saveAll(posts.stream().map(this::toEntity).toList());
  }

  @Override
  public void delete(PostId postId) throws PostNotFoundException {
    try {
      this.repository.deleteById(postId.value());
    } catch (EmptyResultDataAccessException e) {
      throw new PostNotFoundException();
    }
  }

  @Override
  public List<Post> getAll() {
    return this.repository.findAllByOrderByOrderAsc().stream()
        .map(this.postEntityConverter::toDomain)
        .toList();
  }

  @Override
  public Optional<Post> get(PostId postId) {
    return this.repository.findById(postId.value()).map(this.postEntityConverter::toDomain);
  }

  @Override
  public int numberOfPosts() {
    return Math.toIntExact(this.repository.count());
  }

  private PostEntity toEntity(Post post) {
    PostEntity postEntity = this.repository.findById(post.id().value()).orElse(new PostEntity());

    postEntity.increaseVersion(post.version());

    postEntity.id = post.id().value();
    postEntity.emailPrefix = post.emailPrefix().value();

    if (postEntity.postName == null) {
      postEntity.postName = new TextEntity();
    }

    postEntity.postName.apply(post.name());

    postEntity.order = post.order().value();

    return postEntity;
  }
}
