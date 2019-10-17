package it.chalmers.delta.controller;

import it.chalmers.delta.db.entity.Post;
import it.chalmers.delta.response.GetMultiplePostsResponse;
import it.chalmers.delta.response.GetPostResponse;
import it.chalmers.delta.response.PostDoesNotExistResponse;
import it.chalmers.delta.service.PostService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Post> getPost(@PathVariable("id") String id) {
        Post post = this.postService.getPost(UUID.fromString(id));
        if (post == null) {
            throw new PostDoesNotExistResponse();
        }
        return new GetPostResponse(post);
    }


    /**
     * gets all posts in the system.
     *
     * @return all posts currently in the system
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Post>> getPosts() {
        return new GetMultiplePostsResponse(this.postService.getAllPosts());
    }
}
