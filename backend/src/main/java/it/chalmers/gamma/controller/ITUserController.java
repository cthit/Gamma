package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITGroupToSuperGroupDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.user.WhitelistDTO;
import it.chalmers.gamma.requests.ChangeUserPassword;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.requests.DeleteMeRequest;
import it.chalmers.gamma.requests.EditITUserRequest;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.user.EditedProfilePictureResponse;
import it.chalmers.gamma.response.user.GetAllITUsersMinifiedResponse;
import it.chalmers.gamma.response.user.GetAllITUsersMinifiedResponse.GetAllITUsersMinifiedResponseObject;
import it.chalmers.gamma.response.user.GetITUserMinifiedResponse;
import it.chalmers.gamma.response.user.GetITUserResponse;
import it.chalmers.gamma.response.user.GetITUserResponse.GetITUserResponseObject;
import it.chalmers.gamma.response.user.IncorrectCidOrPasswordResponse;
import it.chalmers.gamma.response.user.PasswordChangedResponse;
import it.chalmers.gamma.response.user.PasswordTooShortResponse;
import it.chalmers.gamma.response.user.UserAlreadyExistsResponse;
import it.chalmers.gamma.response.user.UserCreatedResponse;
import it.chalmers.gamma.response.user.UserDeletedResponse;
import it.chalmers.gamma.response.user.UserEditedResponse;
import it.chalmers.gamma.service.ActivationCodeService;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.UserWebsiteService;
import it.chalmers.gamma.service.WhitelistService;
import it.chalmers.gamma.util.ImageITUtils;
import it.chalmers.gamma.util.InputValidationUtils;

import java.io.IOException;
import java.security.Principal;
import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.http.MediaType;
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
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public final class ITUserController {

    private final ITUserService itUserService;
    private final ActivationCodeService activationCodeService;
    private final WhitelistService whitelistService;
    private final UserWebsiteService userWebsiteService;
    private final MembershipService membershipService;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;

    public ITUserController(ITUserService itUserService,
                            ActivationCodeService activationCodeService,
                            WhitelistService whitelistService,
                            UserWebsiteService userWebsiteService,
                            MembershipService membershipService,
                            FKITGroupToSuperGroupService fkitGroupToSuperGroupService) {
        this.itUserService = itUserService;
        this.activationCodeService = activationCodeService;
        this.whitelistService = whitelistService;
        this.userWebsiteService = userWebsiteService;
        this.membershipService = membershipService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
    }

    @PostMapping("/create")
    @ResponseBody
    @SuppressWarnings("PMD.CyclomaticComplexity")
    // TODO, move checks to service, and return only if checks failed or passed
    public UserCreatedResponse createUser(@Valid @RequestBody CreateITUserRequest createITUserRequest,
                                          BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        WhitelistDTO user = this.whitelistService.getWhitelistDTO(createITUserRequest.getWhitelist().getCid());

        if (this.itUserService.userExists(user.getCid())) {
            throw new UserAlreadyExistsResponse();
        }
        if (!this.activationCodeService.codeMatches(createITUserRequest.getCode(), user.getCid())) {
            throw new CodeOrCidIsWrongResponse();
        }
        int minPassLength = 8;

        if (createITUserRequest.getPassword().length() < minPassLength) {
            throw new PasswordTooShortResponse();
        } else {
            this.itUserService.createUser(
                    createITUserRequest.getNick(),
                    createITUserRequest.getFirstName(),
                    createITUserRequest.getLastName(),
                    createITUserRequest.getWhitelist().getCid(),
                    Year.of(createITUserRequest.getAcceptanceYear()),
                    createITUserRequest.isUserAgreement(),
                    null,
                    createITUserRequest.getPassword());
            this.removeCidFromWhitelist(createITUserRequest);
            return new UserCreatedResponse();
        }
    }


    // Check if this cascades automatically
    private void removeCidFromWhitelist(CreateITUserRequest createITUserRequest) {
        this.activationCodeService.deleteCode(createITUserRequest.getWhitelist().getCid());
        this.whitelistService.removeWhiteListedCID(createITUserRequest.getWhitelist().getCid());
    }

    @GetMapping("/me")
    public GetITUserResponseObject getMe(Principal principal) {
        String cid = principal.getName();
        ITUserDTO user = this.itUserService.loadUser(cid);
        //  List<WebsiteDTO> websites =
        //          this.userWebsiteService.getWebsitesOrdered(
        //                  this.userWebsiteService.getWebsites(user)
        //          );
        List<FKITGroupDTO> groups = this.membershipService.getMembershipsByUser(user)
                .stream().map(MembershipDTO::getFkitGroupDTO).collect(Collectors.toList());
        List<FKITGroupToSuperGroupDTO> relationships = groups.stream().map(
                this.fkitGroupToSuperGroupService::getRelationships)
                .flatMap(Collection::stream).collect(Collectors.toList());
        return new GetITUserResponse(user, relationships, null).toResponseObject();
    }

    @GetMapping("/minified")
    public GetAllITUsersMinifiedResponseObject getAllUserMini() {
        List<GetITUserMinifiedResponse> itUsers = this.itUserService.loadAllUsers()
                .stream().map(GetITUserMinifiedResponse::new).collect(Collectors.toList());
        return new GetAllITUsersMinifiedResponse(itUsers).toResponseObject();
    }

    /**
     * First tries to get user using id, if not found gets it using the cid.
     */
    @GetMapping("/{id}")
    public GetITUserResponseObject getUser(@PathVariable("id") String id) {
        ITUserDTO user = this.itUserService.getITUser(id);
        //      List<WebsiteDTO> websites =
        //                this.userWebsiteService.getWebsitesOrdered(
        //                        this.userWebsiteService.getWebsites(user)
        //      );
        List<FKITGroupDTO> groups = this.membershipService.getMembershipsByUser(user)
                .stream().map(MembershipDTO::getFkitGroupDTO).collect(Collectors.toList());
        List<FKITGroupToSuperGroupDTO> relationships = groups.stream().map(
                this.fkitGroupToSuperGroupService::getRelationships)
                .flatMap(Collection::stream).collect(Collectors.toList());
        return new GetITUserResponse(user, relationships, null).toResponseObject();
    }

    @PutMapping("/me")
    public UserEditedResponse editMe(Principal principal, @RequestBody EditITUserRequest request) {
        String cid = principal.getName();
        ITUserDTO user = this.itUserService.loadUser(cid);
        this.itUserService.editUser(user.getId(), request.getNick(), request.getFirstName(), request.getLastName(),
                request.getEmail(), request.getPhone(), request.getLanguage(), request.getAcceptanceYear());
        //  List<WebsiteUrlDTO> websiteURLs = new ArrayList<>();
        //  List<WebsiteInterfaceDTO> userWebsite = new ArrayList<>(
        //          this.userWebsiteService.getWebsites(user)
        //  );
        //  this.userWebsiteService.addWebsiteToUser(user, websiteURLs);
        return new UserEditedResponse();
    }

    @PutMapping("/me/avatar")
    public EditedProfilePictureResponse editProfileImage(Principal principal, @RequestParam MultipartFile file) {
        String cid = principal.getName();
        ITUserDTO user = this.itUserService.loadUser(cid);
        try {
            String fileUrl = ImageITUtils.saveImage(file);
            this.itUserService.editProfilePicture(user, fileUrl);
        } catch (IOException e) {
            throw new FileNotSavedException();
        }

        return new EditedProfilePictureResponse();
    }

    @PutMapping("/me/change_password")
    public PasswordChangedResponse changePassword(Principal principal, @Valid @RequestBody ChangeUserPassword request,
                                                  BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ITUserDTO user = this.extractUser(principal);
        if (!this.itUserService.passwordMatches(user, request.getOldPassword())) {
            throw new IncorrectCidOrPasswordResponse();
        }
        this.itUserService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    @DeleteMapping("/me")
    public UserDeletedResponse deleteMe(Principal principal, @Valid @RequestBody DeleteMeRequest request,
                                        BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ITUserDTO user = this.extractUser(principal);
        if (!this.itUserService.passwordMatches(user, request.getPassword())) {
            throw new IncorrectCidOrPasswordResponse();
        }
        this.userWebsiteService.deleteWebsitesConnectedToUser(
                this.itUserService.getITUser(user.getId().toString())
        );
        this.membershipService.removeAllMemberships(user);
        this.itUserService.removeUser(user.getId());
        return new UserDeletedResponse();
    }

    private ITUserDTO extractUser(Principal principal) {
        return this.itUserService.loadUser(principal.getName());
    }

}
