package it.chalmers.gamma.internal.user.controller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.domain.UserRestricted;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.GroupPost;
import it.chalmers.gamma.domain.Membership;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserCreationService;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.internal.user.service.UserService;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/users")
public final class UserAdminController {

    private final UserService userService;
    private final UserCreationService userCreationService;
    private final MembershipService membershipService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    public UserAdminController(UserService userService,
                               UserCreationService userCreationService,
                               MembershipService membershipFinder) {
        this.userService = userService;
        this.userCreationService = userCreationService;
        this.membershipService = membershipFinder;
    }

    record AdminChangePasswordRequest(UnencryptedPassword password) {}

    @PutMapping("/{id}/change_password")
    public PasswordChangedResponse changePassword(
            @PathVariable("id") UserId id,
            @Valid @RequestBody AdminChangePasswordRequest request) {
        try {
            this.userService.setPassword(id, request.password);
        } catch (UserService.UserNotFoundException e) {
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

    public record GetUserAdminResponse(@JsonUnwrapped User user,
                                       List<GroupPost> groups) { }

    @GetMapping("/{id}")
    public GetUserAdminResponse getUser(@PathVariable("id") UserId id) {
        try {
            User user = this.userService.get(id);
            List<GroupPost> groups = this.toGroupPosts(this.membershipService.getMembershipsByUser(user.id()));

            return new GetUserAdminResponse(user, groups);
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    record UserWithGroups(@JsonUnwrapped UserRestricted user, List<GroupPost> groups) { }

    record GetAllUsersResponse(List<UserWithGroups> users) { }

    @GetMapping()
    public GetAllUsersResponse getAllUsers() {
        List<UserWithGroups> users = this.userService.getAll()
                .stream()
                .map(user -> new UserWithGroups(user, this.toGroupPosts(this.membershipService.getMembershipsByUser(user.id()))))
                .collect(Collectors.toList());

        return new GetAllUsersResponse(users);
    }

    record AdminViewCreateUserRequest(@Valid Cid cid,
                                         UnencryptedPassword password,
                                         Nick nick,
                                         FirstName firstName,
                                         LastName lastName,
                                         Email email,
                                         boolean userAgreement,
                                         int acceptanceYear,
                                         Language language) { }

    @PostMapping()
    public UserCreatedResponse addUser(@Valid @RequestBody AdminViewCreateUserRequest request) {
        this.userCreationService.createUser(new User(
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

    record EditUserRequest (Nick nick,
                            FirstName firstName,
                            LastName lastName,
                            Email email,
                            Language language,
                            int acceptanceYear) { }

    @PutMapping("/{id}")
    public UserEditedResponse editUser(@PathVariable("id") UserId id,
                                       @RequestBody EditUserRequest request) {
        try {
            User user = this.userService.get(id);
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
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    private List<GroupPost> toGroupPosts(List<Membership> memberships) {
        return memberships
                .stream()
                .map(membership -> new GroupPost(membership.post(), membership.group()))
                .collect(Collectors.toList());
    }

    private static class PasswordChangedResponse extends SuccessResponse { }

    private static class UserDeletedResponse extends SuccessResponse { }

    private static class UserCreatedResponse extends SuccessResponse { }

    private static class UserEditedResponse extends SuccessResponse { }

    private static class UserNotFoundResponse extends NotFoundResponse { }

}
