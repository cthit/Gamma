package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.GroupPost;
import it.chalmers.gamma.util.domain.UserWithGroups;
import it.chalmers.gamma.domain.membership.service.MembershipDTO;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.user.service.UserId;
import it.chalmers.gamma.domain.user.service.UserCreationService;
import it.chalmers.gamma.domain.user.service.UserDTO;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.service.UserService;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final MembershipFinder membershipFinder;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    public UserAdminController(UserFinder userFinder,
                               UserService userService,
                               UserCreationService userCreationService,
                               MembershipFinder membershipFinder) {
        this.userFinder = userFinder;
        this.userService = userService;
        this.userCreationService = userCreationService;
        this.membershipFinder = membershipFinder;
    }

    @PutMapping("/{id}/change_password")
    public PasswordChangedResponse changePassword(
            @PathVariable("id") UserId id,
            @Valid @RequestBody AdminChangePasswordRequest request) {
        try {
            this.userService.setPassword(id, request.password);
        } catch (EntityNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UserNotFoundResponse();
        }
        return new PasswordChangedResponse();
    }

    @PutMapping("/{id}")
    public UserEditedResponse editUser(@PathVariable("id") UserId id,
                                           @RequestBody EditITUserRequest request) {
        try {
            this.userService.update(requestToDTO(request, id));
            return new UserEditedResponse();
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    @DeleteMapping("/{id}")
    public UserDeletedResponse deleteUser(@PathVariable("id") UserId id) {
        this.userService.delete(id);
        return new UserDeletedResponse();
    }

    @GetMapping("/{id}")
    public GetUserAdminResponse getUser(@PathVariable("id") UserId id) {
        try {
            UserDTO user = this.userFinder.get(id);
            List<GroupPost> groups = this.toGroupPosts(this.membershipFinder.getMembershipsByUser(user.getId()));

            return new GetUserAdminResponse(user, groups);
        } catch (EntityNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UserNotFoundResponse();
        }
    }

    @GetMapping()
    public GetAllUsersResponse getAllUsers() {
        List<UserWithGroups> users = this.userFinder.getUsersRestricted()
                .stream()
                .map(user -> {
                    try {
                        return new UserWithGroups(user, this.toGroupPosts(this.membershipFinder.getMembershipsByUser(user.getId())));
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return new GetAllUsersResponse(users);
    }

    @PostMapping()
    public UserCreatedResponse addUser(@Valid @RequestBody AdminViewCreateUserRequest request) {
        this.userCreationService.createUser(requestToDTO(request), request.getPassword());
        return new UserCreatedResponse();
    }

    private UserDTO requestToDTO(AdminViewCreateUserRequest request) {
        return new UserDTO.UserDTOBuilder()
                .nick(request.getNick())
                .cid(request.getCid())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .language(request.getLanguage())
                .acceptanceYear(Year.of(request.getAcceptanceYear()))
                .build();
    }

    protected UserDTO requestToDTO(EditITUserRequest request, UserId userId) {
        return new UserDTO.UserDTOBuilder()
                .id(userId)
                .nick(request.nick)
                .firstName(request.firstName)
                .lastName(request.lastName)
                .email(request.email)
                .language(request.language)
                .acceptanceYear(Year.of(request.acceptanceYear))
                .build();
    }

    private List<GroupPost> toGroupPosts(List<MembershipDTO> memberships) {
        return memberships
                .stream()
                .map(membership -> new GroupPost(membership.getPost(), membership.getGroup()))
                .collect(Collectors.toList());
    }

}
