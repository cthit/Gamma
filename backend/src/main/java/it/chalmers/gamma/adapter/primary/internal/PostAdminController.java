package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/posts")
public final class PostAdminController {

    private final PostFacade postFacade;

    public PostAdminController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    @PostMapping()
    public PostCreatedResponse addPost(@RequestBody CreatePostRequest request) {
        this.postFacade.create(
                new PostFacade.NewPost(request.svName, request.enName, request.emailPrefix())
        );
        return new PostCreatedResponse();
    }

    @PutMapping("/{id}")
    public PostEditedResponse editPost(
            @RequestBody EditPostRequest request,
            @PathVariable("id") UUID id) {
        try {
            this.postFacade.update(
                    new PostFacade.UpdatePost(
                            id,
                            request.version,
                            request.svName,
                            request.enName,
                            request.emailPrefix
                    )
            );
            return new PostEditedResponse();
        } catch (PostRepository.PostNotFoundException e) {
            throw new PostNotFoundResponse();
        }
    }

    @DeleteMapping("/{id}")
    public PostDeletedResponse deletePost(@PathVariable("id") UUID id) {
        try {
            this.postFacade.delete(id);
        } catch (PostRepository.PostNotFoundException e) {
            throw new PostNotFoundResponse();
        }
        return new PostDeletedResponse();
    }

    @GetMapping("/{id}/usage")
    public List<GroupFacade.GroupWithMembersDTO> getUsages(@PathVariable("id") UUID id) {
        return this.postFacade.getPostUsages(id);
    }

    private record CreatePostRequest(String svName, String enName, String emailPrefix) {
    }

    private record EditPostRequest(int version, String svName, String enName, String emailPrefix) {
    }

    private static class PostEditedResponse extends SuccessResponse {
    }

    private static class PostDeletedResponse extends SuccessResponse {
    }

    private static class PostCreatedResponse extends SuccessResponse {
    }

    private static class PostNotFoundResponse extends NotFoundResponse {
    }

}
