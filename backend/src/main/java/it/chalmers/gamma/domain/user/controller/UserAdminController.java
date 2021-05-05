package it.chalmers.gamma.domain.user.controller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.user.controller.UserStatusResponses.PasswordChangedResponse;
import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.Language;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.GroupPost;
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

import static it.chalmers.gamma.domain.user.controller.UserStatusResponses.*;

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

    record AdminChangePasswordRequest(String password) {}

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

    @DeleteMapping("/{id}")
    public UserDeletedResponse deleteUser(@PathVariable("id") UserId id) {
        this.userService.delete(id);
        return new UserDeletedResponse();
    }

    @GetMapping("/{id}")
    public GetUserAdminResponse getUser(@PathVariable("id") UserId id) {
        try {
            UserDTO user = this.userFinder.get(id);
            List<GroupPost> groups = this.toGroupPosts(this.membershipFinder.getMembershipsByUser(user.id()));

            return new GetUserAdminResponse(user, groups);
        } catch (EntityNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UserNotFoundResponse();
        }
    }

    record UserWithGroups(@JsonUnwrapped UserRestrictedDTO user, List<GroupPost> groups) { }

    record GetAllUsersResponse(List<UserWithGroups> users) { }

    @GetMapping()
    public GetAllUsersResponse getAllUsers() {
        List<UserWithGroups> users = this.userFinder.getAll()
                .stream()
                .map(user -> {
                    try {
                        return new UserWithGroups(user, this.toGroupPosts(this.membershipFinder.getMembershipsByUser(user.id())));
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return new GetAllUsersResponse(users);
    }

    record AdminViewCreateUserRequest(@Valid Cid cid,
                                         String password,
                                         String nick,
                                         String firstName,
                                         String lastName,
                                         Email email,
                                         boolean userAgreement,
                                         int acceptanceYear,
                                         Language language) { }

    @PostMapping()
    public UserCreatedResponse addUser(@Valid @RequestBody AdminViewCreateUserRequest request) {
        this.userCreationService.createUser(new UserDTO(
                new UserId(),
                request.cid,
                request.email,
                request.language,
                request.nick,
                request.firstName,
                request.lastName,
                request.userAgreement,
                Year.of(request.acceptanceYear),
                true
        ), request.password);
        return new UserCreatedResponse();
    }

    record EditUserRequest (String nick,
                            String firstName,
                            String lastName,
                            Email email,
                            Language language,
                            int acceptanceYear) { }

    @PutMapping("/{id}")
    public UserEditedResponse editUser(@PathVariable("id") UserId id,
                                       @RequestBody EditUserRequest request) {
        try {
            UserDTO user = this.userFinder.get(id);
            this.userService.update(user.with()
                    .nick(request.nick)
                    .firstName(request.firstName)
                    .lastName(request.lastName)
                    .email(request.email)
                    .language(request.language)
                    .acceptanceYear(Year.of(request.acceptanceYear))
                    .build()
            );
            return new UserEditedResponse();
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    private List<GroupPost> toGroupPosts(List<MembershipDTO> memberships) {
        return memberships
                .stream()
                .map(membership -> new GroupPost(membership.post(), membership.group()))
                .collect(Collectors.toList());
    }

}
