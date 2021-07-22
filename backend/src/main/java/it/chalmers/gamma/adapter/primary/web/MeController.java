package it.chalmers.gamma.adapter.primary.web;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.app.MeFacade;
import it.chalmers.gamma.app.domain.FirstName;
import it.chalmers.gamma.app.domain.LastName;
import it.chalmers.gamma.app.domain.Nick;
import it.chalmers.gamma.app.domain.UnencryptedPassword;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.adapter.secondary.userdetails.GrantedAuthorityProxy;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.Language;
import it.chalmers.gamma.app.domain.GroupPost;
import it.chalmers.gamma.util.UserUtils;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/internal/users/me")
public final class MeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);

    private final MeFacade meFacade;

    public MeController(MeFacade meFacade) {
        this.meFacade = meFacade;
    }

    public record GetMeResponse(@JsonUnwrapped User user,
                                List<GroupPost> groups,
                                Collection<GrantedAuthorityProxy> authorities) { }

    @GetMapping()
    public GetMeResponse getMe() {
//        UserDetailsImpl userDetails = UserUtils.getUserDetails();
//        List<GroupPost> groups = this.membershipService.getMembershipsByUser(userDetails.getUser().id())
//                .stream()
//                .map(membership -> new GroupPost(membership.post(), membership.group()))
//                .collect(Collectors.toList());
//
//        return new GetMeResponse(userDetails.getUser(), groups, userDetails.getAuthorities());
        return null;
    }

    public record EditMeRequest (Nick nick,
                                 FirstName firstName,
                                 LastName lastName,
                                 Email email,
                                 Language language) { }

    @PutMapping()
    public UserEditedResponse editMe(@RequestBody EditMeRequest request) {
//        try {
//            User user = UserUtils.getUserDetails().getUser();
//
//            this.userService.update(
//                    user.with()
//                            .nick(request.nick())
//                            .firstName(request.firstName())
//                            .lastName(request.lastName())
//                            .email(request.email())
//                            .language(request.language())
//                            .build()
//            );
//
//        } catch (UserService.UserNotFoundException e) {
//            e.printStackTrace();
//        }

        return new UserEditedResponse();
    }

    record ChangeUserPassword(String oldPassword, UnencryptedPassword password) { }

    @PutMapping("/change_password")
    public PasswordChangedResponse changePassword(@RequestBody ChangeUserPassword request) {
        this.meService.changePassword(request.oldPassword, request.password);
        return new PasswordChangedResponse();
    }

    record DeleteMeRequest (String password) { }

    @DeleteMapping()
    public UserDeletedResponse deleteMe(@RequestBody DeleteMeRequest request) {
        this.meService.deleteAccount(request.password);
        return new UserDeletedResponse();
    }

    @PutMapping("/accept-user-agreement")
    public UserAgreementAccepted acceptUserAgreement(Principal principal) {
        try {
            User user = UserUtils.getUserDetails().getUser();
            this.userService.update(user.withUserAgreement(true));
            return new UserAgreementAccepted();
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    private static class UserAgreementAccepted extends SuccessResponse { }

    private static class UserEditedResponse extends SuccessResponse { }

    private static class PasswordChangedResponse extends SuccessResponse { }

    private static class UserDeletedResponse extends SuccessResponse { }

    private static class UserNotFoundResponse extends NotFoundResponse { }

    private static class IncorrectCidOrPasswordResponse extends ErrorResponse {
        public IncorrectCidOrPasswordResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
