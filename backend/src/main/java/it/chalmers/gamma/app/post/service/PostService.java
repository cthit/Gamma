package it.chalmers.gamma.app.post.service;

import it.chalmers.gamma.app.domain.Post;
import it.chalmers.gamma.app.domain.PostId;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public void create(Post newPost) {
        this.repository.save(new PostEntity(newPost));
    }

    public void update(Post newEdit) throws PostNotFoundException {
        PostEntity post = this.getEntity(newEdit.id());
        post.apply(newEdit);
        this.repository.save(post);
    }

    public void delete(PostId id) {
        this.repository.deleteById(id);
    }

    public boolean exists(PostId postId) {
        return this.repository.existsById(postId);
    }

    public List<Post> getAll() {
        return this.repository.findAll().stream().map(PostEntity::toDTO).collect(Collectors.toList());
    }

    public Post get(PostId id) throws PostNotFoundException {
        return getEntity(id).toDTO();
    }

    protected PostEntity getEntity(PostId id) throws PostNotFoundException {
        return this.repository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    public static class PostNotFoundException extends Exception { }

}
