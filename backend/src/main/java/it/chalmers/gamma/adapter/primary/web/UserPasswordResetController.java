package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.UnencryptedPassword;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.user.UserService;
import it.chalmers.gamma.app.user.UserPasswordResetService;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/internal/users/reset_password")
public class UserPasswordResetController {

    private final UserService userService;
    private final UserPasswordResetService userPasswordResetService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPasswordResetController.class);

    public UserPasswordResetController(UserService userService,
                                       UserPasswordResetService userPasswordResetService) {
        this.userService = userService;
        this.userPasswordResetService = userPasswordResetService;
    }

    private record ResetPasswordRequest(String cidOrEmail) { }

    @PostMapping()
    public PasswordRestLinkSentResponse resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        try {
            this.userPasswordResetService.handlePasswordReset(request.cidOrEmail);
        } catch (UserService.UserNotFoundException e) {
            LOGGER.info("Someone tried to reset password for " + request.cidOrEmail + " but that user doesn't exist");
        }
        return new PasswordRestLinkSentResponse();
    }

    private record ResetPasswordFinishRequest(UnencryptedPassword password,
                                              Cid cid,
                                              String token) { }

    @PutMapping("/finish")
    public PasswordChangedResponse resetPassword(@RequestBody ResetPasswordFinishRequest request) {
        try {
            User user = this.userService.get(request.cid);

            if (!this.userPasswordResetService.tokenMatchesUser(user.id(), request.token)) {
                throw new CodeOrCidIsWrongResponse();
            }

            this.userService.setPassword(user.id(), request.password);
            this.userPasswordResetService.removeToken(user);
        } catch (UserService.UserNotFoundException e) {
            throw new CodeOrCidIsWrongResponse();
        }

        return new PasswordChangedResponse();
    }

    private static class PasswordRestLinkSentResponse extends SuccessResponse { }

    private static class PasswordChangedResponse extends SuccessResponse { }

    private static class CodeOrCidIsWrongResponse extends ErrorResponse {
        private CodeOrCidIsWrongResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
