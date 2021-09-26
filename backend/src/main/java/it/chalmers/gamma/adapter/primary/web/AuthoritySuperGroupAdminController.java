package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/authority/supergroup")
public final class AuthoritySuperGroupAdminController {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AuthoritySuperGroupAdminController(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    private record CreateAuthoritySuperGroupRequest(UUID superGroupId, String authorityLevelName) { }

    @PostMapping
    public AuthoritySuperGroupCreatedResponse addAuthority(@RequestBody CreateAuthoritySuperGroupRequest request) {
        this.authorityLevelFacade.addSuperGroupToAuthorityLevel(
                request.authorityLevelName,
                request.superGroupId
        );
        return new AuthoritySuperGroupCreatedResponse();
    }

    @DeleteMapping
    public AuthoritySuperGroupRemovedResponse removeAuthority(@RequestParam("superGroupId") UUID superGroupId,
                                                              @RequestParam("authorityLevelName") String authorityLevelName) {
        this.authorityLevelFacade.removeSuperGroupFromAuthorityLevel(
                authorityLevelName,
                superGroupId
        );
        return new AuthoritySuperGroupRemovedResponse();
    }

    private static class AuthoritySuperGroupRemovedResponse extends SuccessResponse { }

    private static class AuthoritySuperGroupCreatedResponse extends SuccessResponse { }

    private static class AuthoritySuperGroupNotFoundResponse extends NotFoundResponse { }

    private static class AuthoritySuperGroupAlreadyExistsResponse extends AlreadyExistsResponse { }


}
