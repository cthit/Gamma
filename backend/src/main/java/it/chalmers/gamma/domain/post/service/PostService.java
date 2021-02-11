package it.chalmers.gamma.domain.post.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import it.chalmers.gamma.domain.text.Text;

import it.chalmers.gamma.domain.post.data.Post;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.post.data.PostRepository;
import it.chalmers.gamma.domain.post.controller.response.PostDoesNotExistResponse;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository repository;
    private final PostFinder finder;

    public PostService(PostRepository repository, PostFinder finder) {
        this.repository = repository;
        this.finder = finder;
    }

    public void addPost(PostDTO newPost) {
        this.repository.save(new Post(newPost));
    }

    public void editPost(PostDTO newEdit) throws PostNotFoundException, IDsNotMatchingException {
        Post post = this.finder.getPostEntity(newEdit);
        post.apply(newEdit);
        this.repository.save(post);
    }

    public void deletePost(UUID id) throws PostNotFoundException {
        if (!this.finder.postExists(id)) {
            throw new PostNotFoundException();
        }

        this.repository.deleteById(id);
    }

}
