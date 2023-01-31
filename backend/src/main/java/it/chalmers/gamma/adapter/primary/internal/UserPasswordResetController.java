package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.passwordreset.UserResetPasswordFacade;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal/users/reset_password")
public class UserPasswordResetController {

    private final UserResetPasswordFacade userResetPasswordFacade;

    public UserPasswordResetController(UserResetPasswordFacade userResetPasswordFacade) {
        this.userResetPasswordFacade = userResetPasswordFacade;
    }

    @PostMapping()
    public PasswordRestLinkSentResponse resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        try {
            this.userResetPasswordFacade.startResetPasswordProcess(request.cidOrEmail);
        } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
            throw new PasswordResetProcessErrorResponse();
        }
        return new PasswordRestLinkSentResponse();
    }

    @PutMapping("/finish")
    public PasswordChangedResponse resetPassword(@RequestBody ResetPasswordFinishRequest request) {
        try {
            this.userResetPasswordFacade.finishResetPasswordProcess(request.cidOrEmail, request.token, request.password);
        } catch (UserResetPasswordFacade.PasswordResetProcessException e) {
            throw new PasswordResetProcessErrorResponse();
        }
        return new PasswordChangedResponse();
    }

    private record ResetPasswordRequest(String cidOrEmail) {
    }

    private record ResetPasswordFinishRequest(String password,
                                              String cidOrEmail,
                                              String token) {
    }

    private static class PasswordRestLinkSentResponse extends SuccessResponse {
    }

    private static class PasswordChangedResponse extends SuccessResponse {
    }

    private static class PasswordResetProcessErrorResponse extends ErrorResponse {
        private PasswordResetProcessErrorResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
