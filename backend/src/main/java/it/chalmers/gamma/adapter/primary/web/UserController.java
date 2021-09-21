package it.chalmers.gamma.adapter.primary.web;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.domain.user.AcceptanceYear;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.user.FirstName;
import it.chalmers.gamma.domain.user.Language;
import it.chalmers.gamma.domain.useractivation.UserActivationToken;
import it.chalmers.gamma.domain.user.LastName;
import it.chalmers.gamma.domain.user.Nick;
import it.chalmers.gamma.domain.user.UnencryptedPassword;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.User;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping()
    public List<UserFacade.UserDTO> getAllRestrictedUsers() {
        return this.userFacade.getAll();
    }

    public record GetUserRestrictedResponse(@JsonUnwrapped User user
//                                            List<GroupPost> groups
    ) { }

    @GetMapping("/{id}")
    public GetUserRestrictedResponse getRestrictedUser(@PathVariable("id") UserId id) {
//        try {
//            User user = this.userService.get(id);
//            List<GroupPost> groups = this.membershipService
//                    .getMembershipsByUser(id)
//                    .stream()
//                    .map(membership -> new GroupPost(membership.post(), membership.group()))
//                    .collect(Collectors.toList());
//
//            return new GetUserRestrictedResponse(user, groups);
//        } catch (UserService.UserNotFoundException e) {
//            throw new UserNotFoundResponse();
//        }
        return null;
    }

    record CreateUserRequest (UserActivationToken token,
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
    public UserCreatedResponse createUser(@RequestBody CreateUserRequest request) {
//        try {
//            this.userCreationService.createUserByCode(new User(
//                            UserId.generate(),
//                            request.cid,
//                            request.email,
//                            request.language,
//                            request.nick,
//                            request.firstName,
//                            request.lastName,
//                            request.userAgreement,
//                            request.acceptanceYear
//                    ),
//                    request.password,
//                    request.token
//            );
//        } catch (CidOrCodeNotMatchException e) {
//             If anything is wrong, throw generic error
//            throw new CodeOrCidIsWrongResponse();
//        }
//        return new UserCreatedResponse();
        return null;
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
