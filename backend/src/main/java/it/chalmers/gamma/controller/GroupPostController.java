package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.response.post.GetMultiplePostsResponse;
import it.chalmers.gamma.response.post.GetMultiplePostsResponse.GetMultiplePostsResponseObject;
import it.chalmers.gamma.response.post.GetPostResponse;
import it.chalmers.gamma.response.post.GetPostResponse.GetPostResponseObject;
import it.chalmers.gamma.service.PostService;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups/posts")
public class GroupPostController {

    private final PostService postService;

    public GroupPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public GetPostResponseObject getPost(@PathVariable("id") String id) {
        PostDTO post = this.postService.getPostDTO(id);
        return new GetPostResponse(post).toResponseObject();
    }


    /**
     * gets all posts in the system.
     *
     * @return all posts currently in the system
     */
    @GetMapping()
    public GetMultiplePostsResponseObject getPosts() {
        return new GetMultiplePostsResponse(this.postService.getAllPosts().stream()
                .map(GetPostResponse::new).collect(Collectors.toList())).toResponseObject();
    }
}
