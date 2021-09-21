package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.domain.group.EmailPrefix;
import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.post.Post;

import it.chalmers.gamma.app.post.PostRepository;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        this.postFacade.create(
                new Post(PostId.generate(), request.post(), request.emailPrefix())
        );
        return new PostCreatedResponse();
    }

    @PutMapping("/{id}")
    public PostEditedResponse editPost(
            @RequestBody CreateOrEditPost request,
            @PathVariable("id") PostId id) {
        try {
            this.postFacade.update(new Post(id, request.post(), request.emailPrefix()));
            return new PostEditedResponse();
        } catch (PostRepository.PostNotFoundException e) {
            throw new PostNotFoundResponse();
        }
    }

    @DeleteMapping("/{id}")
    public PostDeletedResponse deletePost(@PathVariable("id") PostId id) {
        try {
            this.postFacade.delete(id);
        } catch (PostRepository.PostNotFoundException e) {
            throw new PostNotFoundResponse();
        }
        return new PostDeletedResponse();
    }

//    @GetMapping("/{id}/usage")
//    public List<Group> getPostUsages(@PathVariable("id") PostId postId) {
//        return this.membershipService.getGroupsWithPost(postId);
//    }

    private static class PostEditedResponse extends SuccessResponse { }

    private static class PostDeletedResponse extends SuccessResponse { }

    private static class PostCreatedResponse extends SuccessResponse {}

    private static class PostNotFoundResponse extends NotFoundResponse { }

}
