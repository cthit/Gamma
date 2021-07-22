package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.util.response.BadRequestResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/useragreement")
public class UserAgreementAdminController {

//    private final ResetUserAgreement resetUserAgreement;
//
//    public UserAgreementAdminController(ResetUserAgreement resetUserAgreement) {
//        this.resetUserAgreement = resetUserAgreement;
//    }

    private record ConfirmPassword(String password) { }

    @PostMapping
    public UserAgreementHasBeenResetResponse resetUserAgreement(@RequestBody ConfirmPassword password) {
//        try {
//            this.resetUserAgreement.resetUserAgreement(password.password);
//        } catch (ResetUserAgreement.IncorrectPasswordException e) {
//            throw new IncorrectPasswordResponse();
//        }
        return new UserAgreementHasBeenResetResponse();
    }

    public static class UserAgreementHasBeenResetResponse extends SuccessResponse { }

    public static class IncorrectPasswordResponse extends BadRequestResponse { }

}
