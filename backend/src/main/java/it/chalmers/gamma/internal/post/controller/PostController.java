package it.chalmers.gamma.internal.post.controller;

import it.chalmers.gamma.internal.post.service.PostDTO;

import it.chalmers.gamma.internal.post.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public List<PostDTO> getPosts() {
        return this.postService.getAll();
    }
}
