package it.chalmers.gamma.domain.post.controller;

import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.post.controller.response.GetMultiplePostsResponse;
import it.chalmers.gamma.domain.post.controller.response.GetMultiplePostsResponse.GetMultiplePostsResponseObject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups/posts")
public class PostController {

    private final PostFinder postFinder;

    public PostController(PostFinder postFinder) {
        this.postFinder = postFinder;
    }

    @GetMapping()
    public GetMultiplePostsResponseObject getPosts() {
        return new GetMultiplePostsResponse(
                this.postFinder.getAll()
        ).toResponseObject();
    }
}
