package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.requests.ResetPasswordFinishRequest;
import it.chalmers.gamma.requests.ResetPasswordRequest;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.PasswordChangedResponse;
import it.chalmers.gamma.response.PasswordResetResponse;
import it.chalmers.gamma.response.UserNotFoundResponse;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.PasswordResetService;
import it.chalmers.gamma.util.InputValidationUtils;
import it.chalmers.gamma.util.TokenUtils;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> resetPasswordRequest(
            @Valid @RequestBody ResetPasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        String userCredentials = request.getCid(); // CID can either be CID or email.
        ITUser user = this.findByCidOrEmail(userCredentials);
        if (user == null) {
            throw new UserNotFoundResponse();
        }

        String token = TokenUtils.generateToken(60,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.NUMBERS);

        if (this.passwordResetService.userHasActiveReset(user)) {
            this.passwordResetService.editToken(user, token);
        } else {
            this.passwordResetService.addToken(user, token);
        }
        sendMail(user, token);
        return new PasswordResetResponse();
    }

    @RequestMapping (value = "/finish", method = RequestMethod.PUT)
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordFinishRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        String userCredentials = request.getCid();
        ITUser user = this.findByCidOrEmail(userCredentials);
        if (user == null) {
            throw new CodeOrCidIsWrongResponse();
        }
        if (!this.passwordResetService.userHasActiveReset(user)
                || !this.passwordResetService.tokenMatchesUser(user, request.getToken())) {
            throw new CodeOrCidIsWrongResponse();
        }
        this.itUserService.setPassword(user, request.getPassword());
        this.passwordResetService.removeToken(user);
        return new PasswordChangedResponse();
    }

    // TODO Make sure that an URL is added to the email
    private void sendMail(ITUser user, String token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token;
        this.mailSenderService.trySendingMail(user.getEmail(), subject, message);
    }

    private ITUser findByCidOrEmail(String userCredentials) {
        ITUser user = this.itUserService.loadUser(userCredentials);
        if (user == null) {
            return this.itUserService.getUserByEmail(userCredentials);
        }
        return user;
    }
}
