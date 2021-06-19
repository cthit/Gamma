package it.chalmers.gamma.internal.useragreement.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.internal.appsettings.service.AppSettingsService;
import it.chalmers.gamma.internal.user.service.UserService;
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

    private final UserService userService;
    private final AppSettingsService appSettingsService;

    public UserAgreementAdminController(UserService userService,
                                        AppSettingsService appSettingsService) {
        this.userService = userService;
        this.appSettingsService = appSettingsService;
    }

    private record ConfirmPassword(String password) { }

    @PostMapping
    public UserAgreementHasBeenResetResponse resetUserAgreement(Principal principal, @RequestBody ConfirmPassword password) {
        try {
            User user = this.userService.get(Cid.valueOf(principal.getName()));

            if (!this.userService.passwordMatches(user.id(), password.password)) {
                throw new IncorrectPasswordResponse();
            }

        } catch (UserService.UserNotFoundException e) {
            e.printStackTrace();
        }

        return new UserAgreementHasBeenResetResponse();
    }

    public static class UserAgreementHasBeenResetResponse extends SuccessResponse { }

    public static class IncorrectPasswordResponse extends BadRequestResponse { }

}
