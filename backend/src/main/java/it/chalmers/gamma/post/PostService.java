package it.chalmers.gamma.post;

import it.chalmers.gamma.domain.text.Text;

import it.chalmers.gamma.post.response.PostDoesNotExistResponse;

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

    public PostDTO addPost(Text postName) {
        return this.repository.save(new Post(postName, null)).toDTO();
    }

    public PostDTO addPost(Text postName, String emailPrefix) {
        return this.repository.save(new Post(postName, emailPrefix)).toDTO();
    }

    public PostDTO addPost(UUID id, Text postName, String emailPrefix) {
        Post post = new Post(postName, emailPrefix);
        post.setId(id);
        return this.repository.save(post).toDTO();
    }

    public PostDTO editPost(PostDTO postDTO, Text postName, String emailPrefix) {
        Post post = this.finder.getPostEntity(postDTO);
        post.setSVPostName(postName.getSv());
        post.setENPostName(postName.getEn());
        post.setEmailPrefix(emailPrefix);
        return this.repository.save(post).toDTO();
    }

    public void deletePost(UUID id) {
        if (!this.finder.postExists(id.toString())) {
            throw new PostDoesNotExistResponse();
        }

        this.repository.deleteById(id);
    }

}
