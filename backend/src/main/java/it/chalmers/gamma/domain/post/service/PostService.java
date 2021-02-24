package it.chalmers.gamma.domain.post.service;

import it.chalmers.gamma.domain.CreateEntity;
import it.chalmers.gamma.domain.DeleteEntity;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.UpdateEntity;
import it.chalmers.gamma.domain.post.PostId;

import it.chalmers.gamma.domain.post.data.Post;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.post.data.PostRepository;

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
        this.repository.save(new Post(newPost));
    }

    public void update(PostDTO newEdit) throws EntityNotFoundException {
        Post post = this.finder.getEntity(newEdit);
        post.apply(newEdit);
        this.repository.save(post);
    }

    public void delete(PostId id) {
        this.repository.deleteById(id);
    }

}
