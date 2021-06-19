package it.chalmers.gamma.internal.userpasswordreset.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.internal.userpasswordreset.service.PasswordResetService;

import javax.validation.Valid;

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
    private final PasswordResetService passwordResetService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPasswordResetController.class);

    public UserPasswordResetController(UserService userService,
                                       PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    private record ResetPasswordRequest(String cidOrEmail) { }

    @PostMapping()
    public PasswordRestLinkSentResponse resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        try {
            this.passwordResetService.handlePasswordReset(request.cidOrEmail);
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

            if (!this.passwordResetService.tokenMatchesUser(user.id(), request.token)) {
                throw new CodeOrCidIsWrongResponse();
            }

            this.userService.setPassword(user.id(), request.password);
            this.passwordResetService.removeToken(user);
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
