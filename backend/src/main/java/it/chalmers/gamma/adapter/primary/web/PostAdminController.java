package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.PostFacade;
import it.chalmers.gamma.app.domain.EmailPrefix;
import it.chalmers.gamma.app.domain.Group;
import it.chalmers.gamma.app.domain.Text;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.Post;

import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/posts")
public final class PostAdminController {

    private final PostFacade postFacade;

    public PostAdminController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    private record CreateOrEditPost(Text post, EmailPrefix emailPrefix) { }

    @PostMapping()
    public PostCreatedResponse addPost(@RequestBody CreateOrEditPost request) {
        this.postService.create(
                new Post(PostId.generate(), request.post(), request.emailPrefix())
        );
        return new PostCreatedResponse();
    }

    @PutMapping("/{id}")
    public PostEditedResponse editPost(
            @RequestBody CreateOrEditPost request,
            @PathVariable("id") PostId id) {
        try {
            this.postService.update(new Post(id, request.post(), request.emailPrefix()));
            return new PostEditedResponse();
        } catch (PostService.PostNotFoundException e) {
            throw new PostNotFoundResponse();
        }
    }

    @DeleteMapping("/{id}")
    public PostDeletedResponse deletePost(@PathVariable("id") PostId id) {
        this.postService.delete(id);
        return new PostDeletedResponse();
    }

    @GetMapping("/{id}/usage")
    public List<Group> getPostUsages(@PathVariable("id") PostId postId) {
        return this.membershipService.getGroupsWithPost(postId);
    }

    private static class PostEditedResponse extends SuccessResponse { }

    private static class PostDeletedResponse extends SuccessResponse { }

    private static class PostCreatedResponse extends SuccessResponse {}

    private static class PostNotFoundResponse extends NotFoundResponse { }

}
