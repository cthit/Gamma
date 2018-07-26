package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    private PostRepository repository;

    public PostService(PostRepository repository){
        this.repository = repository;
    }

    public Post getPost(String post){
        return repository.getByPostName_Sv(post);
    }
    public Post addPost(Text postName){
        Post post = new Post();
        return setPost(post, postName);
    }
    public Post editPost(Post post, Text text){
        return setPost(post, text);
    }
    private Post setPost(Post post, Text postName){
        post.setSVPostName(postName.getSv());
        post.setENPostName(postName.getEn());
        return repository.save(post);
    }
    public boolean postExists(String postName){
        return !(repository.getByPostName_Sv(postName) == null);
    }
    public List<Post> getAllPosts(){
        return repository.findAll();
    }
    public Post getPostById(String id){
        Optional<Post> post = repository.findById(UUID.fromString(id));
        return post.orElse(null);
    }
}
