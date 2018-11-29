package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.PostRepository;

import java.util.List;
import java.util.Objects;
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
        return setPost(post, postName);
    }

    public Post editPost(Post post, Text text) {
        return setPost(post, text);
    }

    private Post setPost(Post post, Text postName) {
        post.setSVPostName(postName.getSv());
        post.setENPostName(postName.getEn());
        return this.repository.save(post);
    }

    public boolean postExists(String postName) {
        return !(this.repository.getByPostName_Sv(postName) == null);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostService that = (PostService) o;
        return this.repository.equals(that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.repository);
    }

    @Override
    public String toString() {
        return "PostService{"
            + "repository=" + this.repository
            + '}';
    }
}
