package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.controller.response.*;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.exception.CidOrCodeNotMatchException;
import it.chalmers.gamma.domain.user.service.UserCreationService;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.controller.request.CreateITUserRequest;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.util.InputValidationUtils;

import java.io.IOException;
import java.time.Year;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(UserFinder userFinder,
                          UserCreationService userCreationService,
                          MembershipFinder membershipFinder) {
        this.userFinder = userFinder;
        this.membershipFinder = membershipFinder;
        this.userCreationService = userCreationService;
    }

    @PostMapping("/create")
    @ResponseBody
    public UserCreatedResponse createUser(@Valid @RequestBody CreateITUserRequest request,
                                          BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        try {
            this.userCreationService.createUserByCode(requestToDTO(request), request.getPassword(), request.getCode());
        } catch (CidOrCodeNotMatchException e) {
            // If anything is wrong, throw generic error
            throw new CodeOrCidIsWrongResponse();
        }
        return new UserCreatedResponse();
    }

    @GetMapping("/{id}")
    public GetUserRestrictedResponse getUser(@PathVariable("id") UserId id) {
        try {
            return new GetUserRestrictedResponse(this.membershipFinder.getUserRestrictedWithMemberships(id));
        } catch (EntityNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UserNotFoundResponse();
        }
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

    private UserDTO requestToDTO(CreateITUserRequest request) {
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
