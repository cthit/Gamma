package it.chalmers.gamma.internal.authority.post.controller;

import it.chalmers.gamma.internal.authority.post.service.AuthorityPostPK;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostShallowDTO;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostService;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.domain.SuperGroupId;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/authority/post")
public final class AuthorityPostAdminController {

    private final AuthorityPostService authorityPostService;

    public AuthorityPostAdminController(AuthorityPostService authorityPostService) {
        this.authorityPostService = authorityPostService;
    }

    private record CreateAuthorityPostRequest(PostId postId, SuperGroupId superGroupId, AuthorityLevelName authority) { }

    @PostMapping
    public AuthorityPostCreatedResponse addAuthority(@RequestBody CreateAuthorityPostRequest request) {
        try {
            this.authorityPostService.create(
                    new AuthorityPostShallowDTO(
                        request.superGroupId,
                        request.postId,
                        request.authority
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

    private static class AuthorityPostNotFoundResponse extends ErrorResponse {
        private AuthorityPostNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

    private static class AuthorityPostAlreadyExistsResponse extends ErrorResponse {
        private AuthorityPostAlreadyExistsResponse() {
            super(HttpStatus.CONFLICT);
        }
    }

}

