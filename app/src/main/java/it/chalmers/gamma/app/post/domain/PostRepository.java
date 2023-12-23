package it.chalmers.gamma.app.post.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    void save(Post post);

    void delete(PostId postId) throws PostNotFoundException;

    List<Post> getAll();

    Optional<Post> get(PostId postId);

    class PostNotFoundException extends Exception {
    }

}
