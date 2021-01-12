package it.chalmers.gamma.post;

import it.chalmers.gamma.post.response.GetMultiplePostsResponse;
import it.chalmers.gamma.post.response.GetMultiplePostsResponse.GetMultiplePostsResponseObject;
import it.chalmers.gamma.post.response.GetPostResponse;
import it.chalmers.gamma.post.response.GetPostResponse.GetPostResponseObject;

import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
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
