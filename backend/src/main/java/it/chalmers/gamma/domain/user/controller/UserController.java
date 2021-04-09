package it.chalmers.gamma.domain.user.controller;

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
    public ResponseEntity<GetAllUsersMinifiedResponse> getAllRestrictedUsers() {
        return ResponseUtils.toResponseObject(new GetAllUsersMinifiedResponse(this.userFinder.getUsersRestricted()));
    }

    @GetMapping("/{id}")
    public GetUserRestrictedResponse getRestrictedUser(@PathVariable("id") UserId id) {
        try {
            UserRestrictedDTO user = new UserRestrictedDTO(this.userFinder.get(id));
            List<GroupPost> groups = this.membershipFinder
                    .getMembershipsByUser(id)
                    .stream()
                    .map(membership -> new GroupPost(membership.getPost(), membership.getGroup()))
                    .collect(Collectors.toList());

            return new GetUserRestrictedResponse(new UserWithGroups(user, groups));
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public UserCreatedResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            this.userCreationService.createUserByCode(requestToDTO(request), request.getPassword(), request.getCode());
        } catch (CidOrCodeNotMatchException e) {
            // If anything is wrong, throw generic error
            throw new CodeOrCidIsWrongResponse();
        }
        return new UserCreatedResponse();
    }


    @GetMapping("/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UserId id, HttpServletResponse response) throws IOException {
        try {
            UserDTO user = this.userFinder.get(id);
            response.sendRedirect(user.getAvatarUrl());
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundResponse();
        }
    }

    private UserDTO requestToDTO(CreateUserRequest request) {
        return new UserDTO.UserDTOBuilder()
                .activated(true)
                .userAgreement(request.isUserAgreement())
                .email(new Email(request.getEmail()))
                .language(request.getLanguage())
                .nick(request.getNick())
                .cid(new Cid(request.getCid()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .acceptanceYear(Year.of(request.getAcceptanceYear()))
                .build();
    }

}
