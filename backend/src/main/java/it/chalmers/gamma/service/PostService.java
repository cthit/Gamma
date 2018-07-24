package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
        post.setPostName(postName);
        return repository.save(post);
    }
    public boolean postExists(String postName){
        return !(repository.getByPostName_Sv(postName) == null);
    }
    public List<Post> getAllPosts(){
        return repository.findAll();
    }
}
