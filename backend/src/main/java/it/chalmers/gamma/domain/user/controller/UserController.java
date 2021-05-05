package it.chalmers.gamma.domain.user.controller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.activationcode.service.Code;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.user.service.UserId;
import it.chalmers.gamma.domain.user.service.UserDTO;
import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;
import it.chalmers.gamma.domain.user.service.CidOrCodeNotMatchException;
import it.chalmers.gamma.domain.user.service.UserCreationService;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.util.domain.*;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import it.chalmers.gamma.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static it.chalmers.gamma.domain.user.controller.UserStatusResponses.*;

@RestController
@RequestMapping("/users")
public final class UserController {

    private final UserFinder userFinder;
    private final UserCreationService userCreationService;
    private final MembershipFinder membershipFinder;

    public UserController(UserFinder userFinder,
                          UserCreationService userCreationService,
                          MembershipFinder membershipFinder) {
        this.userFinder = userFinder;
        this.membershipFinder = membershipFinder;
        this.userCreationService = userCreationService;
    }

    @GetMapping()
    public GetAllUsersMinifiedResponse getAllRestrictedUsers() {
        return new GetAllUsersMinifiedResponse(this.userFinder.getAll());
    }

    public record GetUserRestrictedResponse(@JsonUnwrapped UserRestrictedDTO user, List<GroupPost> groups) { }

    @GetMapping("/{id}")
    public GetUserRestrictedResponse getRestrictedUser(@PathVariable("id") UserId id) {
        try {
            UserRestrictedDTO user = new UserRestrictedDTO(this.userFinder.get(id));
            List<GroupPost> groups = this.membershipFinder
                    .getMembershipsByUser(id)
                    .stream()
                    .map(membership -> new GroupPost(membership.post(), membership.group()))
                    .collect(Collectors.toList());

            return new GetUserRestrictedResponse(user, groups);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    record CreateUserRequest (Code code,
                              String password,
                              String nick,
                              String firstName,
                              Email email,
                              String lastName,
                              boolean userAgreement,
                              int acceptanceYear,
                              Cid cid,
                              Language language) {}

    @PostMapping("/create")
    @ResponseBody
    public UserCreatedResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            this.userCreationService.createUserByCode(new UserDTO(
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
                    ),
                    request.password,
                    request.code
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
//            UserDTO user = this.userFinder.get(id);
//            response.sendRedirect(user.avatarUrl());
//        } catch (EntityNotFoundException e) {
//            throw new UserNotFoundResponse();
//        }
    }

}
