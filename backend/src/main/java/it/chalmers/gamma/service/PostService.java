package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private PostRepository repository;

    public PostService(PostRepository repository){
        this.repository = repository;
    }

    public Post getPost(String post){
        return repository.getByPostName(post);
    }
    public Post addPost(String postName){
        Post post = new Post();
        post.setPostName(postName);
        return repository.save(post);
    }
    public boolean postExists(String postName){
        return !(repository.getByPostName(postName) == null);
    }

}
