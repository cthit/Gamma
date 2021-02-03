package it.chalmers.gamma.user.controller;

import it.chalmers.gamma.requests.ChangeUserPassword;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.controller.request.DeleteMeRequest;
import it.chalmers.gamma.user.controller.request.EditITUserRequest;
import it.chalmers.gamma.user.controller.response.*;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.util.InputValidationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/users/me")
public class MeController {

    @PutMapping("/me")
    public UserEditedResponse editMe(Principal principal, @RequestBody EditITUserRequest request) {
        UserDTO user = extractUser(principal);
        this.userService.editUser(user.getId(), request.getNick(), request.getFirstName(), request.getLastName(),
                request.getEmail(), request.getPhone(), request.getLanguage(), request.getAcceptanceYear());
        return new UserEditedResponse();
    }

    @PutMapping("/me/avatar")
    public EditedProfilePictureResponse editProfileImage(Principal principal, @RequestParam MultipartFile file) {
        UserDTO user = extractUser(principal);
        if (user == null) {
            throw new UserNotFoundResponse();
        } else {
            this.userService.editProfilePicture(user, file);
            return new EditedProfilePictureResponse();
        }

    }

    @PutMapping("/me/change_password")
    public PasswordChangedResponse changePassword(Principal principal, @Valid @RequestBody ChangeUserPassword request,
                                                  BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        UserDTO user = this.extractUser(principal);
        if (!this.userService.passwordMatches(user, request.getOldPassword())) {
            throw new IncorrectCidOrPasswordResponse();
        }
        this.userService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    @DeleteMapping("/me")
    public UserDeletedResponse deleteMe(Principal principal, @Valid @RequestBody DeleteMeRequest request,
                                        BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        UserDTO user = this.extractUser(principal);
        if (!this.userService.passwordMatches(user, request.getPassword())) {
            throw new IncorrectCidOrPasswordResponse();
        }
        this.membershipService.removeAllMemberships(user);
        this.userService.removeUser(user.getId());
        return new UserDeletedResponse();
    }

}
