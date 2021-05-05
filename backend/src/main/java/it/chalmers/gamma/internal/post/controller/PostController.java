package it.chalmers.gamma.internal.post.controller;

import it.chalmers.gamma.internal.post.service.PostDTO;
import it.chalmers.gamma.internal.post.service.PostFinder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups/posts")
public class PostController {

    private final PostFinder postFinder;

    public PostController(PostFinder postFinder) {
        this.postFinder = postFinder;
    }

    @GetMapping()
    public List<PostDTO> getPosts() {
        return this.postFinder.getAll();
    }
}
