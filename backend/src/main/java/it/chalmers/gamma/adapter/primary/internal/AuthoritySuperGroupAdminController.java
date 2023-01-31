package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/authority/supergroup")
public final class AuthoritySuperGroupAdminController {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AuthoritySuperGroupAdminController(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    @PostMapping
    public AuthoritySuperGroupCreatedResponse addAuthority(@RequestBody CreateAuthoritySuperGroupRequest request) {
        try {
            this.authorityLevelFacade.addSuperGroupToAuthorityLevel(
                    request.authorityLevelName,
                    request.superGroupId
            );
        } catch (AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
            throw new AuthorityLevelNotFoundResponse();
        } catch (AuthorityLevelFacade.SuperGroupNotFoundException e) {
            e.printStackTrace();
        }
        return new AuthoritySuperGroupCreatedResponse();
    }

    @DeleteMapping
    public AuthoritySuperGroupRemovedResponse removeAuthority(@RequestParam("superGroupId") UUID superGroupId,
                                                              @RequestParam("authorityLevelName") String authorityLevelName) {
        try {
            this.authorityLevelFacade.removeSuperGroupFromAuthorityLevel(
                    authorityLevelName,
                    superGroupId
            );
        } catch (AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
            throw new AuthorityLevelNotFoundResponse();
        }
        return new AuthoritySuperGroupRemovedResponse();
    }

    private record CreateAuthoritySuperGroupRequest(UUID superGroupId, String authorityLevelName) {
    }

    private static class AuthoritySuperGroupRemovedResponse extends SuccessResponse {
    }

    private static class AuthoritySuperGroupCreatedResponse extends SuccessResponse {
    }

    private static class AuthorityLevelNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthoritySuperGroupNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthoritySuperGroupAlreadyExistsResponse extends AlreadyExistsResponse {
    }


}
