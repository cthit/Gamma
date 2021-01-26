package it.chalmers.gamma.user;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.requests.AdminChangePasswordRequest;
import it.chalmers.gamma.requests.AdminViewCreateITUserRequest;
import it.chalmers.gamma.user.request.EditITUserRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.response.GetAllITUsersResponse;
import it.chalmers.gamma.user.response.GetAllITUsersResponse.GetAllITUsersResponseObject;
import it.chalmers.gamma.user.response.GetITUserResponse;
import it.chalmers.gamma.user.response.GetITUserResponse.GetITUserResponseObject;
import it.chalmers.gamma.user.response.PasswordChangedResponse;
import it.chalmers.gamma.user.response.UserAlreadyExistsResponse;
import it.chalmers.gamma.user.response.UserCreatedResponse;
import it.chalmers.gamma.user.response.UserDeletedResponse;
import it.chalmers.gamma.user.response.UserEditedResponse;
import it.chalmers.gamma.user.response.UserNotFoundResponse;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.time.Year;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.ExcessiveImports"})
@RestController
@RequestMapping("/admin/users")
public final class UserAdminController {

    private final UserFinder userFinder;
    private final UserService userService;
    private final MembershipService membershipService;

    public UserAdminController(UserFinder userFinder, UserService userService, MembershipService membershipService) {
        this.userFinder = userFinder;
        this.userService = userService;
        this.membershipService = membershipService;
    }

    @PutMapping("/{id}/change_password")
    public PasswordChangedResponse changePassword(
            @PathVariable("id") String id,
            @Valid @RequestBody AdminChangePasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        UserDTO user = this.userFinder.getUser(UUID.fromString(id));
        this.userService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    @PutMapping("/{id}")
    public UserEditedResponse editUser(@PathVariable("id") String id,
                                           @RequestBody EditITUserRequest request) {
        if (!this.userFinder.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        this.userService.editUser(
                UUID.fromString(id),
                request.getNick(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getLanguage(),
                request.getAcceptanceYear()
        );
        return new UserEditedResponse();
    }

    @DeleteMapping("/{id}")
    public UserDeletedResponse deleteUser(@PathVariable("id") String id) {
        UserDTO user = this.userFinder.getUser(UUID.fromString(id));
        this.membershipService.removeAllMemberships(user);
        this.userService.removeUser(user.getId());
        return new UserDeletedResponse();
    }

    @GetMapping("/{id}")
    public GetITUserResponseObject getUser(@PathVariable("id") String id) {
        UserDTO user = this.userFinder.getUser(UUID.fromString(id));
        List<GroupDTO> groups = this.membershipService.getUsersGroupDTO(user);
        return new GetITUserResponse(user, groups).toResponseObject();
    }

    @GetMapping()
    public GetAllITUsersResponseObject getAllUsers() {

        List<UserDTO> users = this.userService.getAllUsers();
        List<GetITUserResponse> userResponses = users.stream()
                .map(u -> new GetITUserResponse(u, this.membershipService.getUsersGroupDTO(u)))
                .collect(Collectors.toList());
        return new GetAllITUsersResponse(userResponses).toResponseObject();
    }

    /**
     * Administrative function that can add user without need for user to add it personally.
     */
    @PostMapping()
    public UserCreatedResponse addUser(
            @Valid @RequestBody AdminViewCreateITUserRequest createITUserRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.userFinder.userExists(new Cid(createITUserRequest.getCid()))) {
            throw new UserAlreadyExistsResponse();
        }
        this.userService.createUser(
                createITUserRequest.getNick(),
                createITUserRequest.getFirstName(),
                createITUserRequest.getLastName(),
                createITUserRequest.getCid(),
                Year.of(createITUserRequest.getAcceptanceYear()),
                createITUserRequest.isUserAgreement(),
                createITUserRequest.getEmail(),
                createITUserRequest.getPassword()
        );
        return new UserCreatedResponse();
    }
}
