package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/admin/authority/post")
public final class AuthorityPostAdminController {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AuthorityPostAdminController(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    private record CreateAuthorityPostRequest(PostId postId, SuperGroupId superGroupId, AuthorityLevelName authorityLevelName) { }

    @PostMapping
    public AuthorityPostCreatedResponse addAuthority(@RequestBody CreateAuthorityPostRequest request) {
        this.authorityLevelFacade.addToAuthorityLevel(request.authorityLevelName, request.superGroupId);
        return new AuthorityPostCreatedResponse();
    }

    @DeleteMapping
    public AuthorityPostRemovedResponse removeAuthority(@RequestParam("superGroupId") SuperGroupId superGroupId,
                                                        @RequestParam("postId") PostId postId,
                                                        @RequestParam("authorityLevelName") AuthorityLevelName authorityLevelName) {
        this.authorityLevelFacade.removeFromAuthorityLevel(authorityLevelName, superGroupId, postId);
        return new AuthorityPostRemovedResponse();
    }

    private static class AuthorityPostRemovedResponse extends SuccessResponse { }

    private static class AuthorityPostCreatedResponse extends SuccessResponse { }

    private static class AuthorityPostNotFoundResponse extends NotFoundResponse { }

    private static class AuthorityPostAlreadyExistsResponse extends AlreadyExistsResponse { }

}

