package it.chalmers.gamma.user;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.whitelist.WhitelistDTO;
import it.chalmers.gamma.filter.JwtAuthenticationFilter;
import it.chalmers.gamma.requests.ChangeUserPassword;
import it.chalmers.gamma.user.request.CreateITUserRequest;
import it.chalmers.gamma.user.request.DeleteMeRequest;
import it.chalmers.gamma.user.request.EditITUserRequest;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.response.EditedProfilePictureResponse;
import it.chalmers.gamma.user.response.GetAllITUsersMinifiedResponse;
import it.chalmers.gamma.user.response.GetAllITUsersMinifiedResponse.GetAllITUsersMinifiedResponseObject;
import it.chalmers.gamma.user.response.GetITUserMinifiedResponse;
import it.chalmers.gamma.user.response.GetITUserResponse;
import it.chalmers.gamma.user.response.GetITUserResponse.GetITUserResponseObject;
import it.chalmers.gamma.user.response.GetITUserRestrictedResponse;
import it.chalmers.gamma.user.response.IncorrectCidOrPasswordResponse;
import it.chalmers.gamma.user.response.PasswordChangedResponse;
import it.chalmers.gamma.user.response.PasswordTooShortResponse;
import it.chalmers.gamma.user.response.UserAlreadyExistsResponse;
import it.chalmers.gamma.user.response.UserCreatedResponse;
import it.chalmers.gamma.user.response.UserDeletedResponse;
import it.chalmers.gamma.user.response.UserEditedResponse;
import it.chalmers.gamma.user.response.UserNotFoundResponse;
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

@SuppressWarnings("PMD.ExcessiveImports")
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
    @SuppressWarnings("PMD.CyclomaticComplexity")
    // TODO, move checks to service, and return only if checks failed or passed
    public UserCreatedResponse createUser(@Valid @RequestBody CreateITUserRequest createITUserRequest,
                                          BindingResult result) {
        try {
            if (result.hasErrors()) {
                throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
            }
            WhitelistDTO user = this.whitelistService.getWhitelist(createITUserRequest.getWhitelist().getCid());

            if (this.userFinder.userExists(new Cid(user.getCid()))) {
                throw new UserAlreadyExistsResponse();
            }
            if (!this.activationCodeService.codeMatches(createITUserRequest.getCode(), user.getCid())) {
                throw new CodeOrCidIsWrongResponse();
            }
            int minPassLength = 8;

            if (createITUserRequest.getPassword().length() < minPassLength) {
                throw new PasswordTooShortResponse();
            } else {
                this.userService.createUser(
                        createITUserRequest.getNick(),
                        createITUserRequest.getFirstName(),
                        createITUserRequest.getLastName(),
                        createITUserRequest.getWhitelist().getCid(),
                        Year.of(createITUserRequest.getAcceptanceYear()),
                        createITUserRequest.isUserAgreement(),
                        createITUserRequest.getEmail(),

                        createITUserRequest.getPassword());
                this.whitelistService.removeWhiteListedCID(createITUserRequest.getWhitelist().getCid());
                this.activationCodeService.deleteCode(createITUserRequest.getWhitelist().getCid());
                return new UserCreatedResponse();
            }
        } catch (WhitelistDoesNotExistsException e) {
            LOGGER.warn(String.format("user %s entered non-valid code", createITUserRequest.getNick()));
            return new UserCreatedResponse();
        }
    }

    @GetMapping("/me")
    public GetITUserResponseObject getMe(Principal principal) {
        UserDTO user = extractUser(principal);
        List<GroupDTO> groups = this.membershipService.getMembershipsByUser(user)
                .stream().map(MembershipDTO::getFkitGroupDTO).collect(Collectors.toList());
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

    @PutMapping("/me")
    public UserEditedResponse editMe(Principal principal, @RequestBody EditITUserRequest request) {
        UserDTO user = extractUser(principal);
        this.userService.editUser(user.getId(), request.getNick(), request.getFirstName(), request.getLastName(),
                request.getEmail(), request.getPhone(), request.getLanguage(), request.getAcceptanceYear());
        return new UserEditedResponse();
    }

    @PutMapping("/me/avatar")
    public EditedProfilePictureResponse editProfileImage(Principal principal, @RequestParam MultipartFile file) {
        UserDTO user = extractUser(principal);
        if (user == null) {
            throw new UserNotFoundResponse();
        } else {
            this.userService.editProfilePicture(user, file);
            return new EditedProfilePictureResponse();
        }

    }

    @PutMapping("/me/change_password")
    public PasswordChangedResponse changePassword(Principal principal, @Valid @RequestBody ChangeUserPassword request,
                                                  BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        UserDTO user = this.extractUser(principal);
        if (!this.userService.passwordMatches(user, request.getOldPassword())) {
            throw new IncorrectCidOrPasswordResponse();
        }
        this.userService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    @DeleteMapping("/me")
    public UserDeletedResponse deleteMe(Principal principal, @Valid @RequestBody DeleteMeRequest request,
                                        BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        UserDTO user = this.extractUser(principal);
        if (!this.userService.passwordMatches(user, request.getPassword())) {
            throw new IncorrectCidOrPasswordResponse();
        }
        this.membershipService.removeAllMemberships(user);
        this.userService.removeUser(user.getId());
        return new UserDeletedResponse();
    }

    private UserDTO extractUser(Principal principal) {
        return this.userFinder.getUser(new Cid(principal.getName()));
    }

}
