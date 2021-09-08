package it.chalmers.gamma.app.post;

import it.chalmers.gamma.domain.post.Post;
import it.chalmers.gamma.domain.post.PostId;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    void create(Post post);
    void save(Post post) throws PostNotFoundException;
    void delete(PostId postId) throws PostNotFoundException;

    List<Post> getAll();
    Optional<Post> get(PostId postId);

    class PostNotFoundException extends Exception { }

}
