package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.facade.internal.AuthorityLevelFacade;

import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/authority/post")
public final class AuthorityPostAdminController {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AuthorityPostAdminController(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    private record CreateAuthorityPostRequest(UUID postId, UUID superGroupId, String authorityLevelName) { }

    @PostMapping
    public AuthorityPostCreatedResponse addAuthority(@RequestBody CreateAuthorityPostRequest request) {
        this.authorityLevelFacade.addSuperGroupPostToAuthorityLevel(
                request.authorityLevelName,
                request.superGroupId,
                request.postId
        );
        return new AuthorityPostCreatedResponse();
    }

    @DeleteMapping
    public AuthorityPostRemovedResponse removeAuthority(@RequestParam("superGroupId") UUID superGroupId,
                                                        @RequestParam("postId") UUID postId,
                                                        @RequestParam("authorityLevelName") String authorityLevelName) {
        this.authorityLevelFacade.removeSuperGroupPostFromAuthorityLevel(authorityLevelName, superGroupId, postId);
        return new AuthorityPostRemovedResponse();
    }

    private static class AuthorityPostRemovedResponse extends SuccessResponse { }

    private static class AuthorityPostCreatedResponse extends SuccessResponse { }

    private static class AuthorityPostNotFoundResponse extends NotFoundResponse { }

    private static class AuthorityPostAlreadyExistsResponse extends AlreadyExistsResponse { }

}

