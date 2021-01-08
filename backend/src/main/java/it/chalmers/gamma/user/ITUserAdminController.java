package it.chalmers.gamma.user;

import it.chalmers.gamma.domain.group.FKITGroupDTO;
import it.chalmers.gamma.domain.user.ITUserDTO;
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
import it.chalmers.gamma.membership.MembershipService;
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
public final class ITUserAdminController {

    private final ITUserService itUserService;
    private final MembershipService membershipService;

    public ITUserAdminController(
            ITUserService itUserService,
            MembershipService membershipService) {
        this.itUserService = itUserService;
        this.membershipService = membershipService;
    }

    @PutMapping("/{id}/change_password")
    public PasswordChangedResponse changePassword(
            @PathVariable("id") String id,
            @Valid @RequestBody AdminChangePasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ITUserDTO user = this.itUserService.getITUser(id);
        this.itUserService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    @PutMapping("/{id}")
    public UserEditedResponse editUser(@PathVariable("id") String id,
                                           @RequestBody EditITUserRequest request) {
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        this.itUserService.editUser(
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
        ITUserDTO user = this.itUserService.getITUser(id);
        this.membershipService.removeAllMemberships(user);
        this.itUserService.removeUser(user.getId());
        return new UserDeletedResponse();
    }

    @GetMapping("/{id}")
    public GetITUserResponseObject getUser(@PathVariable("id") String id) {
        ITUserDTO user = this.itUserService.getITUser(id);
        List<FKITGroupDTO> groups = this.membershipService.getUsersGroupDTO(user);
        return new GetITUserResponse(user, groups).toResponseObject();
    }

    @GetMapping()
    public GetAllITUsersResponseObject getAllUsers() {

        List<ITUserDTO> users = this.itUserService.loadAllUsers();
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
        if (this.itUserService.userExists(createITUserRequest.getCid())) {
            throw new UserAlreadyExistsResponse();
        }
        this.itUserService.createUser(
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
