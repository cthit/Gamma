package it.chalmers.gamma.user.controller;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.user.dto.UserDTO;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.user.dto.UserRestrictedDTO;
import it.chalmers.gamma.user.service.UserService;
import it.chalmers.gamma.whitelist.WhitelistDTO;
import it.chalmers.gamma.filter.JwtAuthenticationFilter;
import it.chalmers.gamma.requests.ChangeUserPassword;
import it.chalmers.gamma.user.controller.request.CreateITUserRequest;
import it.chalmers.gamma.user.controller.request.DeleteMeRequest;
import it.chalmers.gamma.user.controller.request.EditITUserRequest;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.controller.response.EditedProfilePictureResponse;
import it.chalmers.gamma.user.controller.response.GetAllITUsersMinifiedResponse;
import it.chalmers.gamma.user.controller.response.GetAllITUsersMinifiedResponse.GetAllITUsersMinifiedResponseObject;
import it.chalmers.gamma.user.controller.response.GetITUserMinifiedResponse;
import it.chalmers.gamma.user.controller.response.GetITUserResponse;
import it.chalmers.gamma.user.controller.response.GetITUserResponse.GetITUserResponseObject;
import it.chalmers.gamma.user.controller.response.GetITUserRestrictedResponse;
import it.chalmers.gamma.user.controller.response.IncorrectCidOrPasswordResponse;
import it.chalmers.gamma.user.controller.response.PasswordChangedResponse;
import it.chalmers.gamma.user.controller.response.PasswordTooShortResponse;
import it.chalmers.gamma.user.controller.response.UserAlreadyExistsResponse;
import it.chalmers.gamma.user.controller.response.UserCreatedResponse;
import it.chalmers.gamma.user.controller.response.UserDeletedResponse;
import it.chalmers.gamma.user.controller.response.UserEditedResponse;
import it.chalmers.gamma.user.controller.response.UserNotFoundResponse;
import it.chalmers.gamma.whitelist.response.WhitelistDoesNotExistsException;
import it.chalmers.gamma.activationcode.ActivationCodeService;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.whitelist.WhitelistService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.io.IOException;
import java.security.Principal;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
            Cid cid = new Cid(request.getWhitelist().getCid());

            WhitelistDTO user = this.whitelistService.getWhitelist(cid.value);

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
                this.whitelistService.removeWhiteListedCID(request.getWhitelist().getCid());
                this.activationCodeService.deleteCode(request.getWhitelist().getCid());
                return new UserCreatedResponse();
            }
        } catch (WhitelistDoesNotExistsException e) {
            LOGGER.warn(String.format("user %s entered non-valid code", request.getNick()));
            return new UserCreatedResponse();
        }
    }

    @GetMapping("/me")
    public GetITUserResponseObject getMe(Principal principal) {
        UserDTO user = extractUser(principal);
        List<GroupDTO> groups = this.membershipService.getMembershipsByUser(user)
                .stream().map(MembershipDTO::getGroup).collect(Collectors.toList());
        return new GetITUserResponse(user, groups).toResponseObject();
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
    public GetITUserRestrictedResponse.GetITUserRestrictedResponseObject getUser(@PathVariable("id") String id) {
        UserDTO user = this.userFinder.getUser(UUID.fromString(id));
        List<GroupDTO> groups = this.membershipService.getUsersGroupDTO(user);
        return new GetITUserRestrictedResponse(new UserRestrictedDTO(user), groups)
                .toResponseObject();
    }

    @GetMapping("/{id}/avatar")
    public void getUserAvatar(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        UserDTO user = this.userFinder.getUser(UUID.fromString(id));
        response.sendRedirect(user.getAvatarUrl());
    }

    private UserDTO extractUser(Principal principal) {
        return this.userFinder.getUser(new Cid(principal.getName()));
    }

    private UserDTO requestToDTO(CreateITUserRequest request) {
        return new UserDTO.UserDTOBuilder()
                .userAgreement(request.isUserAgreement())
                .email(new Email(request.getEmail()))
                .language(request.getLanguage())
                .nick(request.getNick())
                .cid(new Cid(request.getWhitelist().getCid()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .acceptanceYear(Year.of(request.getAcceptanceYear()))
                .build();
    }

}
