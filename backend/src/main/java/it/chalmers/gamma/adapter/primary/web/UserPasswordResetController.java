package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.ResetPasswordFacade;
import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.PasswordResetToken;
import it.chalmers.gamma.app.domain.UnencryptedPassword;

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

    private final ResetPasswordFacade resetPasswordFacade;

    public UserPasswordResetController(ResetPasswordFacade resetPasswordFacade) {
        this.resetPasswordFacade = resetPasswordFacade;
    }

    private record ResetPasswordRequest(String cidOrEmail) { }

    @PostMapping()
    public PasswordRestLinkSentResponse resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        try {
            this.resetPasswordFacade.startResetPasswordProcess(toSignInIdentifier(request.cidOrEmail));
        } catch (ResetPasswordFacade.PasswordResetProcessException e) {
            throw new PasswordResetProcessErrorResponse();
        }
        return new PasswordRestLinkSentResponse();
    }

    private record ResetPasswordFinishRequest(UnencryptedPassword password,
                                              String cidOrEmail,
                                              PasswordResetToken token) { }

    @PutMapping("/finish")
    public PasswordChangedResponse resetPassword(@RequestBody ResetPasswordFinishRequest request) {
        try {
            this.resetPasswordFacade.finishResetPasswordProcess(toSignInIdentifier(request.cidOrEmail), request.token, request.password);
        } catch (ResetPasswordFacade.PasswordResetProcessException e) {
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

    private UserSignInIdentifier toSignInIdentifier(String identifier) {
        try {
            return Cid.valueOf(identifier);
        } catch (IllegalArgumentException ignored) { }

        return new Email(identifier);
    }

}
