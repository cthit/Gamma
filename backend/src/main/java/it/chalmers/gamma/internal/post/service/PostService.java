package it.chalmers.gamma.internal.post.service;

import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.UpdateEntity;

import org.springframework.stereotype.Service;

@Service
public class PostService implements CreateEntity<PostDTO>, DeleteEntity<PostId>, UpdateEntity<PostDTO> {

    private final PostRepository repository;
    private final PostFinder finder;

    public PostService(PostRepository repository, PostFinder finder) {
        this.repository = repository;
        this.finder = finder;
    }

    public void create(PostDTO newPost) {
        this.repository.save(new PostEntity(newPost));
    }

    public void update(PostDTO newEdit) throws EntityNotFoundException {
        PostEntity post = this.finder.getEntity(newEdit);
        post.apply(newEdit);
        this.repository.save(post);
    }

    public void delete(PostId id) {
        this.repository.deleteById(id);
    }

}
