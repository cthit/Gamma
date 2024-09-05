package it.chalmers.gamma.app.post.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

  void save(Post post);

  void saveAll(List<Post> posts);

  void delete(PostId postId) throws PostNotFoundException;

  List<Post> getAll();

  Optional<Post> get(PostId postId);

  int numberOfPosts();

  class PostNotFoundException extends Exception {}
}
