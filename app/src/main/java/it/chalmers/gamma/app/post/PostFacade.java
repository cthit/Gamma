package it.chalmers.gamma.app.post;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.group.domain.EmailPrefix;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.domain.Order;
import it.chalmers.gamma.app.post.domain.Post;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PostFacade extends Facade {

  private final PostRepository postRepository;
  private final GroupRepository groupRepository;

  public PostFacade(
      AccessGuard accessGuard, PostRepository postRepository, GroupRepository groupRepository) {
    super(accessGuard);
    this.postRepository = postRepository;
    this.groupRepository = groupRepository;
  }

  public UUID create(NewPost newPost) {
    accessGuard.require(isAdmin());

    PostId postId = PostId.generate();

    if (newPost.svText.isEmpty() || newPost.enText.isEmpty()) {
      throw new IllegalArgumentException("Post names must not be empty");
    }

    this.postRepository.save(
        new Post(
            postId,
            0,
            new Text(newPost.svText, newPost.enText),
            new EmailPrefix(newPost.emailPrefix),
            new Order(this.postRepository.numberOfPosts())));

    return postId.value();
  }

  public void update(UpdatePost updatePost) throws PostRepository.PostNotFoundException {
    accessGuard.require(isAdmin());

    Post oldPost = this.postRepository.get(new PostId(updatePost.postId)).orElseThrow();
    Post newPost =
        oldPost
            .with()
            .version(updatePost.version)
            .name(new Text(updatePost.svText, updatePost.enText))
            .emailPrefix(new EmailPrefix(updatePost.emailPrefix))
            .build();

    this.postRepository.save(newPost);
  }

  public void delete(UUID postId) throws PostRepository.PostNotFoundException {
    accessGuard.require(isAdmin());

    this.postRepository.delete(new PostId(postId));
  }

  public Optional<PostDTO> get(UUID postId) {
    this.accessGuard.require(isSignedIn());

    return this.postRepository.get(new PostId(postId)).map(PostDTO::new);
  }

  public List<PostDTO> getAll() {
    this.accessGuard.require(isSignedIn());

    return this.postRepository.getAll().stream().map(PostDTO::new).toList();
  }

  public List<GroupFacade.GroupWithMembersDTO> getPostUsages(UUID postId) {
    this.accessGuard.require(isSignedIn());

    return this.groupRepository.getAllByPost(new PostId(postId)).stream()
        .map(GroupFacade.GroupWithMembersDTO::new)
        .toList();
  }

  @Transactional
  public void setOrder(List<UUID> orderedPosts) {
    this.accessGuard.require(isAdmin());

    Map<PostId, Post> posts =
        this.postRepository.getAll().stream()
            .collect(Collectors.toMap(Post::id, Function.identity()));

    boolean anythingChanged = false;

    for (int i = 0; i < orderedPosts.size(); i++) {
      UUID orderedPostId = orderedPosts.get(i);
      Post post = posts.get(new PostId(orderedPostId));
      if (post == null) {
        throw new IllegalArgumentException("Unexpected post id");
      }

      anythingChanged = anythingChanged || post.order().value() != i;

      posts.put(post.id(), post.withOrder(new Order(i)));
    }

    if (anythingChanged) {
      this.postRepository.saveAll(new ArrayList<>(posts.values()));
    }
  }

  public record NewPost(String svText, String enText, String emailPrefix) {}

  public record UpdatePost(
      UUID postId, int version, String svText, String enText, String emailPrefix) {}

  public record PostDTO(
      UUID id, int version, String svName, String enName, String emailPrefix, int order) {
    public PostDTO(Post post) {
      this(
          post.id().value(),
          post.version(),
          post.name().sv().value(),
          post.name().en().value(),
          post.emailPrefix().value(),
          post.order().value());
    }
  }
}
