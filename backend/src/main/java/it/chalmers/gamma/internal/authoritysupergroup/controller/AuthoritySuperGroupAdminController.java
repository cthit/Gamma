package it.chalmers.gamma.internal.authoritysupergroup.controller;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.internal.authoritysupergroup.service.AuthoritySuperGroupPK;
import it.chalmers.gamma.internal.authoritysupergroup.service.AuthoritySuperGroupService;
import it.chalmers.gamma.internal.authoritysupergroup.service.AuthoritySuperGroupShallowDTO;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/authority/supergroup")
public class AuthoritySuperGroupAdminController {

    private final AuthoritySuperGroupService authoritySuperGroupService;

    public AuthoritySuperGroupAdminController(AuthoritySuperGroupService authoritySuperGroupService) {
        this.authoritySuperGroupService = authoritySuperGroupService;
    }

    private record CreateAuthoritySuperGroupRequest(SuperGroupId superGroupId, AuthorityLevelName authority) { }

    @PostMapping
    public AuthoritySuperGroupCreatedResponse addAuthority(@RequestBody CreateAuthoritySuperGroupRequest request) {
        try {
            this.authoritySuperGroupService.create(
                    new AuthoritySuperGroupShallowDTO(
                            request.superGroupId,
                            request.authority
                    )
            );
            return new AuthoritySuperGroupCreatedResponse();
        } catch (AuthoritySuperGroupService.AuthoritySuperGroupAlreadyExistsException e) {
            throw new AuthoritySuperGroupAlreadyExistsResponse();
        }
    }

    @DeleteMapping
    public AuthoritySuperGroupRemovedResponse removeAuthority(@RequestParam("superGroupId") SuperGroupId superGroupId,
                                                              @RequestParam("authorityLevelName") AuthorityLevelName authorityLevelName) {
        try {
            this.authoritySuperGroupService.delete(
                    new AuthoritySuperGroupPK(superGroupId, authorityLevelName)
            );
            return new AuthoritySuperGroupRemovedResponse();
        } catch (AuthoritySuperGroupService.AuthoritySuperGroupNotFoundException e) {
            throw new AuthoritySuperGroupNotFoundResponse();
        }
    }

    private static class AuthoritySuperGroupRemovedResponse extends SuccessResponse { }

    private static class AuthoritySuperGroupCreatedResponse extends SuccessResponse { }

    private static class AuthoritySuperGroupNotFoundResponse extends ErrorResponse {
        private AuthoritySuperGroupNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

    private static class AuthoritySuperGroupAlreadyExistsResponse extends ErrorResponse {
        private AuthoritySuperGroupAlreadyExistsResponse() {
            super(HttpStatus.CONFLICT);
        }
    }


}
