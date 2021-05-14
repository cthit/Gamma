package it.chalmers.gamma.internal.authority.controller;

import it.chalmers.gamma.internal.authority.service.post.AuthorityPostDTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.internal.authority.service.post.AuthorityPostPK;
import it.chalmers.gamma.internal.authority.service.post.AuthorityPostShallowDTO;
import it.chalmers.gamma.internal.authority.service.post.AuthorityPostFinder;
import it.chalmers.gamma.internal.authority.service.post.AuthorityPostService;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/authority")
public final class AuthorityAdminController {

    private final AuthorityPostFinder authorityPostFinder;
    private final AuthorityPostService authorityPostService;

    public AuthorityAdminController(AuthorityPostFinder authorityPostFinder, AuthorityPostService authorityPostService) {
        this.authorityPostFinder = authorityPostFinder;
        this.authorityPostService = authorityPostService;
    }

    @GetMapping()
    public List<AuthorityPostDTO> getAllAuthorities() {
        return this.authorityPostFinder.getAll();
    }

    private record CreateAuthorityRequest(PostId postId, SuperGroupId superGroupId, AuthorityLevelName authority) { }

    @PostMapping
    public AuthorityCreatedResponse addAuthority(@RequestBody CreateAuthorityRequest request) {
        try {
            this.authorityPostService.create(
                    new AuthorityPostShallowDTO(
                        request.superGroupId,
                        request.postId,
                        request.authority
                    )
            );
            return new AuthorityCreatedResponse();
        } catch (EntityAlreadyExistsException e) {
            throw new AuthorityAlreadyExistsResponse();
        }
    }

    @DeleteMapping
    public AuthorityRemovedResponse removeAuthority(@RequestParam("superGroupId") SuperGroupId superGroupId,
                                                    @RequestParam("postId") PostId postId,
                                                    @RequestParam("authorityLevelName") String authorityLevelName) {
        try {
            this.authorityPostService.delete(
                    new AuthorityPostPK(superGroupId, postId, new AuthorityLevelName(authorityLevelName))
            );
            return new AuthorityRemovedResponse();
        } catch (EntityNotFoundException e) {
            throw new AuthorityNotFoundResponse();
        }
    }

    private static class AuthorityRemovedResponse extends SuccessResponse { }

    private static class AuthorityCreatedResponse extends SuccessResponse { }

    private static class AuthorityNotFoundResponse extends ErrorResponse {
        private AuthorityNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

    private static class AuthorityAlreadyExistsResponse extends ErrorResponse {
        private AuthorityAlreadyExistsResponse() {
            super(HttpStatus.CONFLICT);
        }
    }

}

