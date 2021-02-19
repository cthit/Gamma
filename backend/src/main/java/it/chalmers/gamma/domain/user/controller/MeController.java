package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.requests.ChangeUserPassword;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.user.controller.request.DeleteMeRequest;
import it.chalmers.gamma.domain.user.controller.request.EditITUserRequest;
import it.chalmers.gamma.domain.user.controller.response.*;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.service.UserService;
import it.chalmers.gamma.util.InputValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/users/me")
public class MeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class);

    private final UserService userService;
    private final UserFinder userFinder;
    private final MembershipFinder membershipFinder;
    private final AuthorityFinder authorityFinder;

    public MeController(UserService userService,
                        UserFinder userFinder,
                        MembershipFinder membershipFinder,
                        AuthorityFinder authorityFinder) {
        this.userService = userService;
        this.userFinder = userFinder;
        this.membershipFinder = membershipFinder;
        this.authorityFinder = authorityFinder;
    }

    @GetMapping()
    public GetMeResponse.GetMeResponseObject getMe(Principal principal) {
        try {
            UserId userId = this.userFinder.getUser(new Cid(principal.getName())).getId();

            return new GetMeResponse(
                    this.membershipFinder.getUserRestrictedWithMemberships(userId),
                    this.authorityFinder.getGrantedAuthorities(userId)
            ).toResponseObject();
        } catch (UserNotFoundException e) {
            LOGGER.error("Signed in user doesn't exist, maybe deleted?", e);
            throw new UserNotFoundResponse();
        }
    }

    @PutMapping()
    public UserEditedResponse editMe(Principal principal, @RequestBody EditITUserRequest request) {
        try {
            UserDTO user = extractUser(principal);

            this.userService.editUser(
                    new UserDTO.UserDTOBuilder()
                            .from(user)
                            .nick(request.getNick())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(new Email(request.getEmail()))
                            .phone(request.getPhone())
                            .language(request.getLanguage())
                            .acceptanceYear(request.getAcceptanceYear())
                            .build()
            );

        } catch (UserNotFoundException | IDsNotMatchingException e) {
            e.printStackTrace();
        }

        return new UserEditedResponse();
    }

    @PutMapping("/avatar")
    public EditedProfilePictureResponse editProfileImage(Principal principal, @RequestParam MultipartFile file) {
        try {
            UserDTO user = extractUser(principal);
            this.userService.editProfilePicture(user, file);
            return new EditedProfilePictureResponse();
        } catch (UserNotFoundException e) {
            LOGGER.error("Cannot find the signed in user", e);
            throw new UserNotFoundResponse();
        }
    }

    @PutMapping("/change_password")
    public PasswordChangedResponse changePassword(Principal principal, @Valid @RequestBody ChangeUserPassword request,
                                                  BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        UserDTO user = null;
        try {
            user = this.extractUser(principal);

            if (!this.userService.passwordMatches(user, request.getOldPassword())) {
                throw new IncorrectCidOrPasswordResponse();
            }

            this.userService.setPassword(user, request.getPassword());
        } catch (UserNotFoundException e) {
            LOGGER.error("Cannot find the signed in user", e);
            throw new UserNotFoundResponse();
        }
        return new PasswordChangedResponse();
    }

    @DeleteMapping()
    public UserDeletedResponse deleteMe(Principal principal, @Valid @RequestBody DeleteMeRequest request,
                                        BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        try {
            UserDTO user = this.extractUser(principal);

            if (!this.userService.passwordMatches(user, request.getPassword())) {
                throw new IncorrectCidOrPasswordResponse();
            }

            this.userService.removeUser(user.getId());

            return new UserDeletedResponse();
        } catch (UserNotFoundException e) {
            LOGGER.error("Can't find user, already deleted?", e);
            throw new UserNotFoundResponse();
        }
    }

    private UserDTO extractUser(Principal principal) throws UserNotFoundException {
        return this.userFinder.getUser(new Cid(principal.getName()));
    }

}
