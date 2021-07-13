package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.adapter.secondary.jpa.authoritypost.AuthorityPostPK;
import it.chalmers.gamma.app.authoritypost.service.AuthorityPostDTO;
import it.chalmers.gamma.app.authority.AuthorityPostService;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.SuperGroupId;

import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/admin/authority/post")
public final class AuthorityPostAdminController {

    private final AuthorityPostService authorityPostService;

    public AuthorityPostAdminController(AuthorityPostService authorityPostService) {
        this.authorityPostService = authorityPostService;
    }

    private record CreateAuthorityPostRequest(PostId postId, SuperGroupId superGroupId, AuthorityLevelName authorityLevelName) { }

    @PostMapping
    public AuthorityPostCreatedResponse addAuthority(@RequestBody CreateAuthorityPostRequest request) {
        try {
            this.authorityPostService.create(
                    new AuthorityPostDTO(
                        request.superGroupId,
                        request.postId,
                        request.authorityLevelName
                    )
            );
            return new AuthorityPostCreatedResponse();
        } catch (AuthorityPostService.AuthorityPostNotFoundException e) {
            throw new AuthorityPostAlreadyExistsResponse();
        }
    }

    @DeleteMapping
    public AuthorityPostRemovedResponse removeAuthority(@RequestParam("superGroupId") SuperGroupId superGroupId,
                                                        @RequestParam("postId") PostId postId,
                                                        @RequestParam("authorityLevelName") AuthorityLevelName authorityLevelName) {
        try {
            this.authorityPostService.delete(
                    new AuthorityPostPK(superGroupId, postId, authorityLevelName)
            );
            return new AuthorityPostRemovedResponse();
        } catch (AuthorityPostService.AuthorityPostNotFoundException e) {
            throw new AuthorityPostNotFoundResponse();
        }
    }

    private static class AuthorityPostRemovedResponse extends SuccessResponse { }

    private static class AuthorityPostCreatedResponse extends SuccessResponse { }

    private static class AuthorityPostNotFoundResponse extends NotFoundResponse { }

    private static class AuthorityPostAlreadyExistsResponse extends AlreadyExistsResponse { }

}

