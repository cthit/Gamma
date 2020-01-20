package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.requests.ResetPasswordFinishRequest;
import it.chalmers.gamma.requests.ResetPasswordRequest;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.user.PasswordChangedResponse;
import it.chalmers.gamma.response.user.PasswordResetResponse;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.PasswordResetService;
import it.chalmers.gamma.util.InputValidationUtils;
import it.chalmers.gamma.util.TokenUtils;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/users/reset_password")
public class UserPasswordResetController {

    private final ITUserService itUserService;
    private final PasswordResetService passwordResetService;
    private final MailSenderService mailSenderService;

    public UserPasswordResetController(
            ITUserService itUserService,
            PasswordResetService passwordResetService,
            MailSenderService mailSenderService) {
        this.itUserService = itUserService;
        this.passwordResetService = passwordResetService;
        this.mailSenderService = mailSenderService;
    }

    @PostMapping()
    public PasswordResetResponse resetPasswordRequest(
            @Valid @RequestBody ResetPasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        String userCredentials = request.getCid(); // CID can either be CID or email.
        ITUserDTO user = this.itUserService.getITUser(userCredentials);
        String token = TokenUtils.generateToken(10,     // TODO Move to service
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS);
        if (this.passwordResetService.userHasActiveReset(user)) {
            this.passwordResetService.editToken(user, token);
        } else {
            this.passwordResetService.addToken(user, token);
        }
        this.sendMail(user, token);
        return new PasswordResetResponse();
    }

    @PutMapping("/finish")
    public PasswordChangedResponse resetPassword(
            @Valid @RequestBody ResetPasswordFinishRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ITUserDTO user = this.itUserService.getITUser(request.getCid());
        if (!this.passwordResetService.userHasActiveReset(user)
                || !this.passwordResetService.tokenMatchesUser(user, request.getToken())) {
            throw new CodeOrCidIsWrongResponse();
        }
        this.itUserService.setPassword(user, request.getPassword());
        this.passwordResetService.removeToken(user);
        return new PasswordChangedResponse();
    }

    // TODO Make sure that an URL is added to the email
    private void sendMail(ITUserDTO user, String token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token;
        this.mailSenderService.trySendingMail(user.getEmail(), subject, message);
    }
}
