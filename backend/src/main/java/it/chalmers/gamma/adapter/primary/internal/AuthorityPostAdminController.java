package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
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

    @PostMapping
    public AuthorityPostCreatedResponse addAuthority(@RequestBody CreateAuthorityPostRequest request) {
        try {
            this.authorityLevelFacade.addSuperGroupPostToAuthorityLevel(
                    request.authorityLevelName,
                    request.superGroupId,
                    request.postId
            );
        } catch (AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
            throw new AuthorityLevelNotFoundResponse();
        } catch (AuthorityLevelFacade.SuperGroupNotFoundException | AuthorityLevelFacade.PostNotFoundException e) {
            throw new AuthorityPostNotFoundResponse();
        }
        return new AuthorityPostCreatedResponse();
    }

    @DeleteMapping
    public AuthorityPostRemovedResponse removeAuthority(@RequestParam("superGroupId") UUID superGroupId,
                                                        @RequestParam("postId") UUID postId,
                                                        @RequestParam("authorityLevelName") String authorityLevelName) {
        try {
            this.authorityLevelFacade.removeSuperGroupPostFromAuthorityLevel(authorityLevelName, superGroupId, postId);
        } catch (AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
            throw new AuthorityPostNotFoundResponse();
        }
        return new AuthorityPostRemovedResponse();
    }

    private record CreateAuthorityPostRequest(UUID postId, UUID superGroupId, String authorityLevelName) {
    }

    private static class AuthorityLevelNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthorityPostRemovedResponse extends SuccessResponse {
    }

    private static class AuthorityPostCreatedResponse extends SuccessResponse {
    }

    private static class AuthorityPostNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthorityPostAlreadyExistsResponse extends AlreadyExistsResponse {
    }

}

