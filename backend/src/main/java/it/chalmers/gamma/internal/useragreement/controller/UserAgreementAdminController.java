package it.chalmers.gamma.internal.useragreement.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.internal.appsettings.service.AppSettingsService;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.internal.useragreement.service.UserAgreementService;
import it.chalmers.gamma.util.response.BadRequestResponse;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/internal/admin/useragreement")
public class UserAgreementAdminController {

    private final UserAgreementService userAgreementService;

    public UserAgreementAdminController(UserAgreementService userAgreementService) {
        this.userAgreementService = userAgreementService;
    }

    private record ConfirmPassword(String password) { }

    @PostMapping
    public UserAgreementHasBeenResetResponse resetUserAgreement(@RequestBody ConfirmPassword password) {
        try {
            this.userAgreementService.resetUserAgreement(password.password);
        } catch (UserAgreementService.IncorrectPasswordException e) {
            throw new IncorrectPasswordResponse();
        }
        return new UserAgreementHasBeenResetResponse();
    }

    public static class UserAgreementHasBeenResetResponse extends SuccessResponse { }

    public static class IncorrectPasswordResponse extends BadRequestResponse { }

}
