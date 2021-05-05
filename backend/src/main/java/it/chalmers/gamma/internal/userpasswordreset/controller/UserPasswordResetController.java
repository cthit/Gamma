package it.chalmers.gamma.internal.userpasswordreset.controller;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.internal.user.service.UserFinder;
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
@RequestMapping("/users/reset_password")
public class UserPasswordResetController {

    private final UserFinder userFinder;
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPasswordResetController.class);

    public UserPasswordResetController(UserFinder userFinder,
                                       UserService userService,
                                       PasswordResetService passwordResetService) {
        this.userFinder = userFinder;
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    private record ResetPasswordRequest(String cidOrEmail) { }

    @PostMapping()
    public PasswordRestLinkSentResponse resetPasswordRequest(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            this.passwordResetService.handlePasswordReset(request.cidOrEmail);
        } catch (EntityNotFoundException e) {
            LOGGER.info("Someone tried to reset password for " + request.cidOrEmail + " but that user doesn't exist");
        }
        return new PasswordRestLinkSentResponse();
    }

    private record ResetPasswordFinishRequest(String password,
                                              Cid cid,
                                              String token) { }

    @PutMapping("/finish")
    public PasswordChangedResponse resetPassword(@Valid @RequestBody ResetPasswordFinishRequest request) {
        try {
            UserDTO user = this.userFinder.get(request.cid);

            if (!this.passwordResetService.tokenMatchesUser(user.id(), request.token)) {
                throw new CodeOrCidIsWrongResponse();
            }

            this.userService.setPassword(user.id(), request.password);
            this.passwordResetService.removeToken(user);
        } catch (EntityNotFoundException e) {
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
