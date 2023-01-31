package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/authority/user")
public final class AuthorityUserAdminController {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AuthorityUserAdminController(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    @PostMapping
    public AuthorityUserCreatedResponse addAuthority(@RequestBody CreateAuthorityUserRequest request) {
        try {
            this.authorityLevelFacade.addUserToAuthorityLevel(
                    request.authorityLevelName,
                    request.userId
            );
        } catch (AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
            throw new AuthorityLevelNotFoundResponse();
        } catch (AuthorityLevelFacade.UserNotFoundException e) {
            throw new AuthorityUserNotFoundResponse();
        }
        return new AuthorityUserCreatedResponse();
    }

    @DeleteMapping
    public AuthorityUserRemovedResponse removeAuthority(@RequestParam("userId") UUID userId,
                                                        @RequestParam("authorityLevelName") String authorityLevelName) {
        try {
            this.authorityLevelFacade.removeUserFromAuthorityLevel(
                    authorityLevelName,
                    userId
            );
        } catch (AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
            throw new AuthorityUserNotFoundResponse();
        }
        return new AuthorityUserRemovedResponse();
    }

    private record CreateAuthorityUserRequest(UUID userId, String authorityLevelName) {
    }

    private static class AuthorityUserRemovedResponse extends SuccessResponse {
    }

    private static class AuthorityUserCreatedResponse extends SuccessResponse {
    }

    private static class AuthorityUserNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthorityLevelNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthorityUserAlreadyExistsResponse extends AlreadyExistsResponse {
    }

}
