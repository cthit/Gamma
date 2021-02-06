package it.chalmers.gamma.user.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.membership.service.MembershipFinder;
import it.chalmers.gamma.requests.ChangeUserPassword;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.controller.request.DeleteMeRequest;
import it.chalmers.gamma.user.controller.request.EditITUserRequest;
import it.chalmers.gamma.user.controller.response.*;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.user.exception.UserNotFoundException;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.user.service.UserService;
import it.chalmers.gamma.util.InputValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/me")
public class MeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);

    private final UserService userService;
    private final UserFinder userFinder;
    private final MembershipFinder membershipFinder;

    public MeController(UserService userService,
                        UserFinder userFinder,
                        MembershipFinder membershipFinder) {
        this.userService = userService;
        this.userFinder = userFinder;
        this.membershipFinder = membershipFinder;
    }

    @GetMapping()
    public GetITUserResponse.GetITUserResponseObject getMe(Principal principal) {
        UserDTO user = extractUser(principal);
        List<GroupDTO> groups = this.membershipFinder.getMembershipsByUser(user)
                .stream().map(MembershipDTO::getGroup).collect(Collectors.toList());
        return new GetITUserResponse(user, groups).toResponseObject();
    }

    @PutMapping()
    public UserEditedResponse editMe(Principal principal, @RequestBody EditITUserRequest request) {
        UserDTO user = extractUser(principal);
        this.userService.editUser(user.getId(), request.getNick(), request.getFirstName(), request.getLastName(),
                request.getEmail(), request.getPhone(), request.getLanguage(), request.getAcceptanceYear());
        return new UserEditedResponse();
    }

    @PutMapping("/avatar")
    public EditedProfilePictureResponse editProfileImage(Principal principal, @RequestParam MultipartFile file) {
        UserDTO user = extractUser(principal);
        if (user == null) {
            throw new UserNotFoundResponse();
        } else {
            this.userService.editProfilePicture(user, file);
            return new EditedProfilePictureResponse();
        }

    }

    @PutMapping("/change_password")
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

    @DeleteMapping()
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

    private UserDTO extractUser(Principal principal) {
        try {
            return this.userFinder.getUser(new Cid(principal.getName()));
        } catch (UserNotFoundException e) {
            LOGGER.error("Signed in user doesn't exist, maybe deleted?", e);
            throw new UserNotFoundResponse();
        }
    }

}
