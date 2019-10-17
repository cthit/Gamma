package it.chalmers.delta.service;

import it.chalmers.delta.db.entity.Post;
import it.chalmers.delta.db.entity.Text;
import it.chalmers.delta.db.repository.PostRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;


/**
 * A class to handle all database access to memberships between users and groups in the system.
 */
@Service
public class PostService {

    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Post addPost(Text postName) {
        Post post = new Post();
        return savePost(post, postName);
    }

    public Post editPost(Post post, Text text) {
        return savePost(post, text);
    }

    private Post savePost(Post post, Text postName) {
        post.setSVPostName(postName.getSv());
        post.setENPostName(postName.getEn());
        return this.repository.save(post);
    }

    public boolean postExists(String postName) {
        return this.repository.getByPostName_Sv(postName) != null;
    }
    public boolean postExists(UUID id) {
        return this.repository.existsById(id);
    }

    public List<Post> getAllPosts() {
        return this.repository.findAll();
    }

    public Post getPost(String post) {
        return this.repository.getByPostName_Sv(post);
    }

    public Post getPost(UUID id) {
        Optional<Post> post = this.repository.findById(id);
        return post.orElse(null);
    }

    public void deletePost(UUID id) {
        this.repository.deleteById(id);
    }

}
