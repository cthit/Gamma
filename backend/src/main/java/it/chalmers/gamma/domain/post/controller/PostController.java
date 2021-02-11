package it.chalmers.gamma.domain.post.controller;

import it.chalmers.gamma.domain.post.controller.response.PostDoesNotExistResponse;
import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import it.chalmers.gamma.domain.post.controller.response.GetMultiplePostsResponse;
import it.chalmers.gamma.domain.post.controller.response.GetMultiplePostsResponse.GetMultiplePostsResponseObject;
import it.chalmers.gamma.domain.post.controller.response.GetPostResponse;
import it.chalmers.gamma.domain.post.controller.response.GetPostResponse.GetPostResponseObject;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups/posts")
public class PostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    private final PostFinder postFinder;

    public PostController(PostFinder postFinder) {
        this.postFinder = postFinder;
    }

    @GetMapping("/{id}")
    public GetPostResponseObject getPost(@PathVariable("id") UUID id) {
        try {
            return new GetPostResponse(
                    this.postFinder.getPost(id)
            ).toResponseObject();
        } catch (PostNotFoundException e) {
            LOGGER.error("Post not found", e);
            throw new PostDoesNotExistResponse();
        }
    }

    @GetMapping()
    public GetMultiplePostsResponseObject getPosts() {
        return new GetMultiplePostsResponse(
                this.postFinder.getPosts()
        ).toResponseObject();
    }
}
