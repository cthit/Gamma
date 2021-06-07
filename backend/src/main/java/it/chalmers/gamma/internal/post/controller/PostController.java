package it.chalmers.gamma.internal.post.controller;

import it.chalmers.gamma.domain.Post;

import it.chalmers.gamma.internal.post.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/groups/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public List<Post> getPosts() {
        return this.postService.getAll();
    }
}
