package it.chalmers.gamma.internal.user.controller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.AcceptanceYear;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.GroupPost;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.ActivationCodeToken;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserRestricted;
import it.chalmers.gamma.internal.user.service.CidOrCodeNotMatchException;
import it.chalmers.gamma.internal.user.service.UserCreationService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public final class UserController {

    private final UserService userService;
    private final UserCreationService userCreationService;
    private final MembershipService membershipService;

    public UserController(UserService userService,
                          UserCreationService userCreationService,
                          MembershipService membershipService) {
        this.userService = userService;
        this.membershipService = membershipService;
        this.userCreationService = userCreationService;
    }

    @GetMapping()
    public List<UserRestricted> getAllRestrictedUsers() {
        return this.userService.getAll();
    }

    public record GetUserRestrictedResponse(@JsonUnwrapped UserRestricted user, List<GroupPost> groups) { }

    @GetMapping("/{id}")
    public GetUserRestrictedResponse getRestrictedUser(@PathVariable("id") UserId id) {
        try {
            UserRestricted user = new UserRestricted(this.userService.get(id));
            List<GroupPost> groups = this.membershipService
                    .getMembershipsByUser(id)
                    .stream()
                    .map(membership -> new GroupPost(membership.post(), membership.group()))
                    .collect(Collectors.toList());

            return new GetUserRestrictedResponse(user, groups);
        } catch (UserService.UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    record CreateUserRequest (ActivationCodeToken token,
                              UnencryptedPassword password,
                              Nick nick,
                              FirstName firstName,
                              Email email,
                              LastName lastName,
                              boolean userAgreement,
                              AcceptanceYear acceptanceYear,
                              Cid cid,
                              Language language) {}

    @PostMapping("/create")
    @ResponseBody
    public UserCreatedResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            this.userCreationService.createUserByCode(new User(
                            UserId.generate(),
                            request.cid,
                            request.email,
                            request.language,
                            request.nick,
                            request.firstName,
                            request.lastName,
                            request.userAgreement,
                            request.acceptanceYear
                    ),
                    request.password,
                    request.token
            );
        } catch (CidOrCodeNotMatchException e) {
            // If anything is wrong, throw generic error
            throw new CodeOrCidIsWrongResponse();
        }
        return new UserCreatedResponse();
    }


    @GetMapping("/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UserId id, HttpServletResponse response) throws IOException {
            ///todo fix
        //        tvcry {
//            UserDTO user = this.userService.get(id);
//            response.sendRedirect(user.avatarUrl());
//        } catch (EntityNotFoundException e) {
//            throw new UserNotFoundResponse();
//        }
    }

    private static class UserCreatedResponse extends SuccessResponse { }

    private static class CodeOrCidIsWrongResponse extends ErrorResponse {
        private CodeOrCidIsWrongResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static class UserNotFoundResponse extends NotFoundResponse { }

}
