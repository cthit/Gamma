package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @DeleteMapping("/approval/{clientUid}")
    public ClientApprovalDeletedResponse deleteClientApproval(@PathVariable("clientUid") UUID clientUid) {
        this.meFacade.deleteUserApproval(clientUid);
        return new ClientApprovalDeletedResponse();
    }

    public record EditMeRequest(String nick,
                                String firstName,
                                String lastName,
                                String email,
                                String language) {
    }

    record ChangeUserPassword(String oldPassword, String password) {
    }

    record DeleteMeRequest(String password) {
    }

    private static class UserAgreementAccepted extends SuccessResponse {
    }

    private static class UserEditedResponse extends SuccessResponse {
    }

    private static class PasswordChangedResponse extends SuccessResponse {
    }

    private static class UserDeletedResponse extends SuccessResponse {
    }

    private static class UserNotFoundResponse extends NotFoundResponse {
    }

    private static class ClientApprovalDeletedResponse extends SuccessResponse {
    }

    private static class IncorrectCidOrPasswordResponse extends ErrorResponse {
        public IncorrectCidOrPasswordResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
