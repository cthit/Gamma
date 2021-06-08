package it.chalmers.gamma.internal.post.controller;

import it.chalmers.gamma.domain.Post;

import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.internal.post.service.PostService;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable("id") PostId id) {
        try {
            return this.postService.get(id);
        } catch (PostService.PostNotFoundException e) {
            throw new PostNotFoundResponse();
        }
    }


    @GetMapping()
    public List<Post> getPosts() {
        return this.postService.getAll();
    }

    private static class PostNotFoundResponse extends NotFoundResponse { }

}
