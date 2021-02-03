package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.passwordreset.request.ResetPasswordFinishRequest;
import it.chalmers.gamma.passwordreset.request.ResetPasswordRequest;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.exception.UserNotFoundException;
import it.chalmers.gamma.user.controller.response.UserNotFoundResponse;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.user.controller.response.PasswordChangedResponse;
import it.chalmers.gamma.user.controller.response.PasswordResetResponse;
import it.chalmers.gamma.user.service.UserService;
import it.chalmers.gamma.passwordreset.PasswordResetService;
import it.chalmers.gamma.util.InputValidationUtils;

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

    private final UserFinder userFinder;
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public UserPasswordResetController(UserFinder userFinder,
                                       UserService userService,
                                       PasswordResetService passwordResetService) {
        this.userFinder = userFinder;
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping()
    public PasswordResetResponse resetPasswordRequest(
            @Valid @RequestBody ResetPasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        String cidOrEmail = request.getCid();
        try {
            this.passwordResetService.handlePasswordReset(cidOrEmail);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
        return new PasswordResetResponse();
    }

    @PutMapping("/finish")
    public PasswordChangedResponse resetPassword(@Valid @RequestBody ResetPasswordFinishRequest request,
                                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        try {
            UserDTO user = this.userFinder.getUser(new Cid(request.getCid()));

            if (!this.passwordResetService.tokenMatchesUser(user.getId(), request.getToken())) {
                throw new CodeOrCidIsWrongResponse();
            }

            this.userService.setPassword(user.getId(), request.getPassword());
            this.passwordResetService.removeToken(user);
        } catch (UserNotFoundException e) {
            throw new CodeOrCidIsWrongResponse();
        }

        return new PasswordChangedResponse();
    }

}
