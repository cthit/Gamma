package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.user.service.UserCreationService;
import it.chalmers.gamma.requests.AdminChangePasswordRequest;
import it.chalmers.gamma.requests.AdminViewCreateUserRequest;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.service.UserService;
import it.chalmers.gamma.domain.user.controller.request.EditITUserRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.user.controller.response.GetAllITUsersResponse;
import it.chalmers.gamma.domain.user.controller.response.GetAllITUsersResponse.GetAllITUsersResponseObject;
import it.chalmers.gamma.domain.user.controller.response.GetITUserResponse;
import it.chalmers.gamma.domain.user.controller.response.GetITUserResponse.GetITUserResponseObject;
import it.chalmers.gamma.domain.user.controller.response.PasswordChangedResponse;
import it.chalmers.gamma.domain.user.controller.response.UserCreatedResponse;
import it.chalmers.gamma.domain.user.controller.response.UserDeletedResponse;
import it.chalmers.gamma.domain.user.controller.response.UserEditedResponse;
import it.chalmers.gamma.domain.user.controller.response.UserNotFoundResponse;
import it.chalmers.gamma.domain.membership.service.MembershipService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.time.Year;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
public final class UserAdminController {

    private final UserFinder userFinder;
    private final UserService userService;
    private final UserCreationService userCreationService;
    private final MembershipService membershipService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    public UserAdminController(UserFinder userFinder, UserService userService, UserCreationService userCreationService, MembershipService membershipService) {
        this.userFinder = userFinder;
        this.userService = userService;
        this.userCreationService = userCreationService;
        this.membershipService = membershipService;
    }

    @PutMapping("/{id}/change_password")
    public PasswordChangedResponse changePassword(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AdminChangePasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        try {
            this.userService.setPassword(id, request.getPassword());
        } catch (UserNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UserNotFoundResponse();
        }
        return new PasswordChangedResponse();
    }

    @PutMapping("/{id}")
    public UserEditedResponse editUser(@PathVariable("id") UUID id,
                                           @RequestBody EditITUserRequest request) {
        try {
            this.userService.editUser(requestToDTO(request, id));
            return new UserEditedResponse();
        } catch (UserNotFoundException | IDsNotMatchingException e) {
            LOGGER.error("Can't find user to edit", e);
            throw new UserNotFoundResponse();
        }
    }

    @DeleteMapping("/{id}")
    public UserDeletedResponse deleteUser(@PathVariable("id") UUID id) {
        try {
            this.userService.removeUser(id);
            return new UserDeletedResponse();
        } catch (UserNotFoundException e) {
            LOGGER.error("Can't find user to delete", e);
            throw new UserNotFoundResponse();
        }
    }

    @GetMapping("/{id}")
    public GetITUserResponseObject getUser(@PathVariable("id") UUID id) {
        try {
            return new GetITUserResponse(this.userFinder.getUserWithMemberships(id)).toResponseObject();
        } catch (UserNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UserNotFoundResponse();
        }
    }

    @GetMapping()
    public GetAllITUsersResponseObject getAllUsers() {
        return new GetAllITUsersResponse(this.userFinder.getUsersWithMembership()).toResponseObject();
    }

    /**
     * Administrative function that can add user without need for user to add it personally.
     */
    @PostMapping()
    public UserCreatedResponse addUser(
            @Valid @RequestBody AdminViewCreateUserRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        this.userCreationService.createUser(requestToDTO(request), request.getPassword());
        return new UserCreatedResponse();
    }

    private UserDTO requestToDTO(AdminViewCreateUserRequest request) {
        return new UserDTO.UserDTOBuilder()
                .nick(request.getNick())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(new Email(request.getEmail()))
                .language(request.getLanguage())
                .acceptanceYear(Year.of(request.getAcceptanceYear()))
                .build();
    }

    protected UserDTO requestToDTO(EditITUserRequest request, UUID userId) {
        return new UserDTO.UserDTOBuilder()
                .id(userId)
                .nick(request.getNick())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(new Email(request.getEmail()))
                .phone(request.getPhone())
                .language(request.getLanguage())
                .acceptanceYear(request.getAcceptanceYear())
                .build();
    }

}
