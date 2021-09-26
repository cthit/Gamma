package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.UserResetPasswordFacade;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.PasswordResetToken;
import it.chalmers.gamma.domain.user.UnencryptedPassword;

import it.chalmers.gamma.app.user.UserSignInIdentifier;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/internal/users/reset_password")
public class UserPasswordResetController {

    private final UserResetPasswordFacade userResetPasswordFacade;

    public UserPasswordResetController(UserResetPasswordFacade userResetPasswordFacade) {
        this.userResetPasswordFacade = userResetPasswordFacade;
    }

    private record ResetPasswordRequest(String cidOrEmail) { }

    @PostMapping()
    public PasswordRestLinkSentResponse resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        try {
            this.userResetPasswordFacade.startResetPasswordProcess(request.cidOrEmail);
        } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
            throw new PasswordResetProcessErrorResponse();
        }
        return new PasswordRestLinkSentResponse();
    }

    private record ResetPasswordFinishRequest(String password,
                                              String cidOrEmail,
                                              String token) { }

    @PutMapping("/finish")
    public PasswordChangedResponse resetPassword(@RequestBody ResetPasswordFinishRequest request) {
        try {
            this.userResetPasswordFacade.finishResetPasswordProcess(request.cidOrEmail, request.token, request.password);
        } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
            throw new PasswordResetProcessErrorResponse();
        }
        return new PasswordChangedResponse();
    }

    private static class PasswordRestLinkSentResponse extends SuccessResponse { }

    private static class PasswordChangedResponse extends SuccessResponse { }

    private static class PasswordResetProcessErrorResponse extends ErrorResponse {
        private PasswordResetProcessErrorResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
