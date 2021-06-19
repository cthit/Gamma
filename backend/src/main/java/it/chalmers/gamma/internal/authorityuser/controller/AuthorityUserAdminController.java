package it.chalmers.gamma.internal.authorityuser.controller;

import it.chalmers.gamma.domain.AuthorityLevelName;
import it.chalmers.gamma.internal.authorityuser.service.AuthorityUserPK;
import it.chalmers.gamma.internal.authorityuser.service.AuthorityUserService;
import it.chalmers.gamma.internal.authorityuser.service.AuthorityUserShallowDTO;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/authority/user")
public class AuthorityUserAdminController {

    private final AuthorityUserService authorityUserService;

    public AuthorityUserAdminController(AuthorityUserService authorityUserService) {
        this.authorityUserService = authorityUserService;
    }

    private record CreateAuthorityUserRequest(UserId userId, AuthorityLevelName authorityLevelName) { }

    @PostMapping
    public AuthorityUserCreatedResponse addAuthority(@RequestBody CreateAuthorityUserRequest request) {
        try {
            this.authorityUserService.create(
                    new AuthorityUserShallowDTO(
                            request.userId,
                            request.authorityLevelName
                    )
            );
        } catch (AuthorityUserService.AuthorityUserNotFoundException e) {
            throw new AuthorityUserAlreadyExistsResponse();
        }
        return new AuthorityUserCreatedResponse();
    }

    @DeleteMapping
    public AuthorityUserRemovedResponse removeAuthority(@RequestParam("userId") UserId userId,
                                                        @RequestParam("authorityLevelName") AuthorityLevelName authorityLevelName) {
        try {
            this.authorityUserService.delete(
                    new AuthorityUserPK(
                            userId,
                            authorityLevelName
                    )
            );
        } catch (AuthorityUserService.AuthorityUserNotFoundException e) {
            throw new AuthorityUserNotFoundResponse();
        }
        return new AuthorityUserRemovedResponse();
    }

    private static class AuthorityUserRemovedResponse extends SuccessResponse { }

    private static class AuthorityUserCreatedResponse extends SuccessResponse { }

    private static class AuthorityUserNotFoundResponse extends NotFoundResponse { }

    private static class AuthorityUserAlreadyExistsResponse extends AlreadyExistsResponse { }

}
