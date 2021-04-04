package it.chalmers.gamma.domain.userpasswordreset.controller;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.user.data.dto.UserDTO;
import it.chalmers.gamma.domain.userpasswordreset.controller.request.ResetPasswordFinishRequest;
import it.chalmers.gamma.domain.userpasswordreset.controller.request.ResetPasswordRequest;
import it.chalmers.gamma.domain.user.controller.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.domain.user.controller.response.UserNotFoundResponse;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.controller.response.PasswordChangedResponse;
import it.chalmers.gamma.domain.user.controller.response.PasswordResetResponse;
import it.chalmers.gamma.domain.user.service.UserService;
import it.chalmers.gamma.domain.userpasswordreset.service.PasswordResetService;

import javax.validation.Valid;

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
    public PasswordResetResponse resetPasswordRequest(@Valid @RequestBody ResetPasswordRequest request) {
        String cidOrEmail = request.getCid();
        try {
            this.passwordResetService.handlePasswordReset(cidOrEmail);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundResponse();
        }
        return new PasswordResetResponse();
    }

    @PutMapping("/finish")
    public PasswordChangedResponse resetPassword(@Valid @RequestBody ResetPasswordFinishRequest request) {
        try {
            UserDTO user = this.userFinder.get(new Cid(request.getCid()));

            if (!this.passwordResetService.tokenMatchesUser(user.getId(), request.getToken())) {
                throw new CodeOrCidIsWrongResponse();
            }

            this.userService.setPassword(user.getId(), request.getPassword());
            this.passwordResetService.removeToken(user);
        } catch (EntityNotFoundException e) {
            throw new CodeOrCidIsWrongResponse();
        }

        return new PasswordChangedResponse();
    }

}
