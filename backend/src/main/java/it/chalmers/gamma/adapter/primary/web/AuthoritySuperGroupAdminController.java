package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.AuthorityFacade;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.adapter.secondary.jpa.authoritylevel.AuthoritySuperGroupPK;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/authority/supergroup")
public final class AuthoritySuperGroupAdminController {

    private final AuthorityFacade authorityFacade;

    public AuthoritySuperGroupAdminController(AuthorityFacade authorityFacade) {
        this.authorityFacade = authorityFacade;
    }

    private record CreateAuthoritySuperGroupRequest(SuperGroupId superGroupId, AuthorityLevelName authorityLevelName) { }

    @PostMapping
    public AuthoritySuperGroupCreatedResponse addAuthority(@RequestBody CreateAuthoritySuperGroupRequest request) {
//        try {
//            this.authoritySuperGroupService.create(
//                    new AuthoritySuperGroupDTO(
//                            request.superGroupId,
//                            request.authorityLevelName
//                    )
//            );
//            return new AuthoritySuperGroupCreatedResponse();
//        } catch (AuthoritySuperGroupService.AuthoritySuperGroupAlreadyExistsException e) {
//            throw new AuthoritySuperGroupAlreadyExistsResponse();
//        }
        return null;
    }

    @DeleteMapping
    public AuthoritySuperGroupRemovedResponse removeAuthority(@RequestParam("superGroupId") SuperGroupId superGroupId,
                                                              @RequestParam("authorityLevelName") AuthorityLevelName authorityLevelName) {
//        try {
//            this.authoritySuperGroupService.delete(
//                    new AuthoritySuperGroupPK(superGroupId, authorityLevelName)
//            );
//            return new AuthoritySuperGroupRemovedResponse();
//        } catch (AuthoritySuperGroupService.AuthoritySuperGroupNotFoundException e) {
//            throw new AuthoritySuperGroupNotFoundResponse();
//        }
        return null;
    }

    private static class AuthoritySuperGroupRemovedResponse extends SuccessResponse { }

    private static class AuthoritySuperGroupCreatedResponse extends SuccessResponse { }

    private static class AuthoritySuperGroupNotFoundResponse extends NotFoundResponse { }

    private static class AuthoritySuperGroupAlreadyExistsResponse extends AlreadyExistsResponse { }


}
