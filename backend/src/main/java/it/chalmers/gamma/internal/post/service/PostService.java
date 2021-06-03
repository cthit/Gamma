package it.chalmers.gamma.internal.post.service;

import it.chalmers.gamma.domain.PostId;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public void create(PostDTO newPost) {
        this.repository.save(new PostEntity(newPost));
    }

    public void update(PostDTO newEdit) throws PostNotFoundException {
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

    public List<PostDTO> getAll() {
        return this.repository.findAll().stream().map(PostEntity::toDTO).collect(Collectors.toList());
    }

    public PostDTO get(PostId id) throws PostNotFoundException {
        return getEntity(id).toDTO();
    }

    protected PostEntity getEntity(PostId id) throws PostNotFoundException {
        return this.repository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    public static class PostNotFoundException extends Exception { }

}
