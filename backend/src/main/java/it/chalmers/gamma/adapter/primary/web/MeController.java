package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.facade.MeFacade;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/users/me")
public final class MeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);

    private final MeFacade meFacade;

    public MeController(MeFacade meFacade) {
        this.meFacade = meFacade;
    }

    @GetMapping()
    public MeFacade.MeDTO getMe() {
        return this.meFacade.getMe();
    }

    public record EditMeRequest (String nick,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String language) { }

    @PutMapping()
    public UserEditedResponse editMe(@RequestBody EditMeRequest request) {
        this.meFacade.updateMe(
                new MeFacade.UpdateMe(
                        request.nick,
                        request.firstName,
                        request.lastName,
                        request.email,
                        request.language
                )
        );
        return new UserEditedResponse();
    }

    record ChangeUserPassword(String oldPassword, String password) { }

    @PutMapping("/change_password")
    public PasswordChangedResponse changePassword(@RequestBody ChangeUserPassword request) {
        this.meFacade.updatePassword(
                new MeFacade.UpdatePassword(
                        request.oldPassword,
                        request.password
                )
        );
        return new PasswordChangedResponse();
    }

    record DeleteMeRequest (String password) { }

    @DeleteMapping()
    public UserDeletedResponse deleteMe(@RequestBody DeleteMeRequest request) {
        this.meFacade.deleteMe(request.password);
        return new UserDeletedResponse();
    }

    @PutMapping("/accept-user-agreement")
    public UserAgreementAccepted acceptUserAgreement() {
        this.meFacade.acceptUserAgreement();
        return new UserAgreementAccepted();
    }

    @GetMapping("/approval")
    public List<MeFacade.UserApprovedClientDTO> getApprovedClientsByUser() {
        return this.meFacade.getSignedInUserApprovals();
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
