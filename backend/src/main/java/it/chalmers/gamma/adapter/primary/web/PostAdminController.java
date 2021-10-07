package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.facade.GroupFacade;
import it.chalmers.gamma.app.facade.PostFacade;

import it.chalmers.gamma.app.port.repository.PostRepository;
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
import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/posts")
public final class PostAdminController {

    private final PostFacade postFacade;

    public PostAdminController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    private record CreatePostRequest(String svText, String enText, String emailPrefix) { }

    @PostMapping()
    public PostCreatedResponse addPost(@RequestBody CreatePostRequest request) {
        this.postFacade.create(
                new PostFacade.NewPost(request.svText, request.enText, request.emailPrefix())
        );
        return new PostCreatedResponse();
    }

    private record EditPostRequest(int version, String svText, String enText, String emailPrefix) { }

    @PutMapping("/{id}")
    public PostEditedResponse editPost(
            @RequestBody EditPostRequest request,
            @PathVariable("id") UUID id) {
        try {
            this.postFacade.update(
                    new PostFacade.UpdatePost(
                            id,
                            request.version,
                            request.svText,
                            request.enText,
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
    public List<GroupFacade.GroupDTO> getUsages(@PathVariable("id") UUID id) {
        return this.postFacade.getPostUsages(id);
    }

    private static class PostEditedResponse extends SuccessResponse { }

    private static class PostDeletedResponse extends SuccessResponse { }

    private static class PostCreatedResponse extends SuccessResponse {}

    private static class PostNotFoundResponse extends NotFoundResponse { }

}
