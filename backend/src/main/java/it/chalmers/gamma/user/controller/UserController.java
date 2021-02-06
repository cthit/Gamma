package it.chalmers.gamma.user.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.user.controller.response.*;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.user.exception.UserNotFoundException;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.user.dto.UserRestrictedDTO;
import it.chalmers.gamma.user.service.UserService;
import it.chalmers.gamma.filter.JwtAuthenticationFilter;
import it.chalmers.gamma.user.controller.request.CreateITUserRequest;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.controller.response.GetAllITUsersMinifiedResponse.GetAllITUsersMinifiedResponseObject;
import it.chalmers.gamma.activationcode.ActivationCodeService;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.whitelist.service.WhitelistService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final UserService userService;
    private final ActivationCodeService activationCodeService;
    private final WhitelistService whitelistService;
    private final MembershipService membershipService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public UserController(UserFinder userFinder,
                          UserService userService,
                          ActivationCodeService activationCodeService,
                          WhitelistService whitelistService,
                          MembershipService membershipService) {
        this.userFinder = userFinder;
        this.userService = userService;
        this.activationCodeService = activationCodeService;
        this.whitelistService = whitelistService;
        this.membershipService = membershipService;
    }

    @PostMapping("/create")
    @ResponseBody
    public UserCreatedResponse createUser(@Valid @RequestBody CreateITUserRequest request,
                                          BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        try {
            Cid cid = new Cid(request.getCid());

            this.userService.createUser(requestToDTO(request), request.getPassword());

            if (this.userFinder.userExists(new Cid(user.getCid()))) {
                throw new UserAlreadyExistsResponse();
            }
            if (!this.activationCodeService.codeMatches(request.getCode(), user.getCid())) {
                throw new CodeOrCidIsWrongResponse();
            }
            int minPassLength = 8;

            if (request.getPassword().length() < minPassLength) {
                throw new PasswordTooShortResponse();
            } else {
                this.userService.createUser(requestToDTO(request), request.getPassword());
                this.whitelistService.removeWhiteListedCid(request.getWhitelist().getCid());
                this.activationCodeService.deleteCode(request.getWhitelist().getCid());
                return new UserCreatedResponse();
            }
        } catch (WhitelistDoesNotExistsResponse e) {
            LOGGER.warn(String.format("user %s entered non-valid code", request.getNick()));
            return new UserCreatedResponse();
        }
    }

    @GetMapping("/minified")
    public GetAllITUsersMinifiedResponseObject getAllUserMini() {
        List<GetITUserMinifiedResponse> itUsers = this.userService
                .getAllUsers()
                .stream()
                .map(UserRestrictedDTO::new)
                .map(GetITUserMinifiedResponse::new)
                .collect(Collectors.toList());
        return new GetAllITUsersMinifiedResponse(itUsers).toResponseObject();
    }

    /**
     * First tries to get user using id, if not found gets it using the cid.
     */
    @GetMapping("/{id}")
    public GetITUserRestrictedResponse.GetITUserRestrictedResponseObject getUser(@PathVariable("id") UUID id) {
        UserDTO user = this.userFinder.getUser(UUID.fromString(id));
        List<GroupDTO> groups = this.membershipService.getUsersGroupDTO(user);
        return new GetITUserRestrictedResponse(new UserRestrictedDTO(user), groups)
                .toResponseObject();
    }

    @GetMapping("/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") UUID id, HttpServletResponse response) throws IOException {
        try {
            UserDTO user = this.userFinder.getUser(id);
            response.sendRedirect(user.getAvatarUrl());
        } catch (UserNotFoundException e) {
            LOGGER.error("User not found", e);
            throw new UserNotFoundResponse();
        }
    }

    private UserDTO requestToDTO(CreateITUserRequest request) {
        return new UserDTO.UserDTOBuilder()
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
