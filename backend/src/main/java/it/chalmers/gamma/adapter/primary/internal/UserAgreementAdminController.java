package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.facade.internal.UserAgreementFacade;
import it.chalmers.gamma.util.response.BadRequestResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/useragreement")
public class UserAgreementAdminController {

    private final UserAgreementFacade userAgreementFacade;

    public UserAgreementAdminController(UserAgreementFacade userAgreementFacade) {
        this.userAgreementFacade = userAgreementFacade;
    }

    private record ConfirmPassword(String password) { }

    @PostMapping
    public UserAgreementHasBeenResetResponse resetUserAgreement(@RequestBody ConfirmPassword password) {
        this.userAgreementFacade.resetUserAgreement(password.password);
        return new UserAgreementHasBeenResetResponse();
    }

    public static class UserAgreementHasBeenResetResponse extends SuccessResponse { }

    public static class IncorrectPasswordResponse extends BadRequestResponse { }

}
