package it.chalmers.delta.controller;

import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.ACCEPTANCE_YEAR;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.CID;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.FIRST_NAME;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.ID;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.LAST_NAME;
import static it.chalmers.delta.db.serializers.ITUserSerializer.Properties.NICK;

import it.chalmers.delta.db.entity.ITUser;
import it.chalmers.delta.db.entity.Membership;
import it.chalmers.delta.db.entity.WebsiteInterface;
import it.chalmers.delta.db.entity.WebsiteURL;
import it.chalmers.delta.db.entity.Whitelist;
import it.chalmers.delta.db.serializers.ITUserSerializer;
import it.chalmers.delta.requests.ChangeUserPassword;
import it.chalmers.delta.requests.CreateGroupRequest;
import it.chalmers.delta.requests.CreateITUserRequest;
import it.chalmers.delta.requests.DeleteMeRequest;
import it.chalmers.delta.requests.EditITUserRequest;
import it.chalmers.delta.response.CodeExpiredResponse;
import it.chalmers.delta.response.CodeOrCidIsWrongResponse;
import it.chalmers.delta.response.EditedProfilePicture;
import it.chalmers.delta.response.FileNotSavedException;
import it.chalmers.delta.response.IncorrectCidOrPasswordResponse;
import it.chalmers.delta.response.InputValidationFailedResponse;
import it.chalmers.delta.response.PasswordChangedResponse;
import it.chalmers.delta.response.PasswordTooShortResponse;
import it.chalmers.delta.response.UserAlreadyExistsResponse;
import it.chalmers.delta.response.UserCreatedResponse;
import it.chalmers.delta.response.UserDeletedResponse;
import it.chalmers.delta.response.UserEditedResponse;
import it.chalmers.delta.response.UserNotFoundResponse;
import it.chalmers.delta.service.ActivationCodeService;
import it.chalmers.delta.service.FKITGroupToSuperGroupService;
import it.chalmers.delta.service.ITUserService;
import it.chalmers.delta.service.MembershipService;
import it.chalmers.delta.service.UserWebsiteService;
import it.chalmers.delta.service.WhitelistService;
import it.chalmers.delta.util.ImageITUtils;
import it.chalmers.delta.util.InputValidationUtils;
import it.chalmers.delta.views.WebsiteView;

import java.io.IOException;
import java.security.Principal;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public ResponseEntity<String> createUser(
            @Valid @RequestBody CreateITUserRequest createITUserRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        Whitelist user = this.whitelistService.getWhitelist(
                createITUserRequest.getWhitelist().getCid()
        );

        if (user == null) {
            throw new UserNotFoundResponse();
        }

        createITUserRequest.setWhitelist(user);

        if (this.itUserService.userExists(createITUserRequest.getWhitelist().getCid())) {
            throw new UserAlreadyExistsResponse();
        }

        if (!this.activationCodeService.codeMatches(createITUserRequest.getCode(), user.getCid())) {
            throw new CodeOrCidIsWrongResponse();
        }

        if (this.activationCodeService.hasCodeExpired(user.getCid(), 2)) {
            this.activationCodeService.deleteCode(user.getCid());
            throw new CodeExpiredResponse();
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
                    createITUserRequest.getPassword(),
                    createITUserRequest.getLanguage());
            removeCidFromWhitelist(createITUserRequest);
            return new UserCreatedResponse();
        }
    }


    // Check if this cascades automatically
    private void removeCidFromWhitelist(CreateITUserRequest createITUserRequest) {
        this.activationCodeService.deleteCode(createITUserRequest.getWhitelist().getCid());
        this.whitelistService.removeWhiteListedCID(createITUserRequest.getWhitelist().getCid());
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public JSONObject getMe(Principal principal) {
        String cid = principal.getName();
        ITUser user = this.itUserService.loadUser(cid);
        ITUserSerializer serializer =
                new ITUserSerializer(ITUserSerializer.Properties.getAllProperties());
        List<WebsiteView> websites =
                this.userWebsiteService.getWebsitesOrdered(
                        this.userWebsiteService.getWebsites(user)
                );
        List<Membership> memberships = this.addSuperGroupInfo(this.membershipService.getMembershipsByUser(user));
        return serializer.serialize(user, websites, ITUserSerializer.getGroupsAsJson(memberships));
    }

    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public List<JSONObject> getAllUserMini() {
        List<ITUser> itUsers = this.itUserService.loadAllUsers();
        List<ITUserSerializer.Properties> props =
                new ArrayList<>(Arrays.asList(
                        CID,
                        FIRST_NAME,
                        LAST_NAME,
                        NICK,
                        ACCEPTANCE_YEAR,
                        ID
                ));
        List<JSONObject> minifiedITUsers = new ArrayList<>();
        ITUserSerializer serializer = new ITUserSerializer(props);
        for (ITUser user : itUsers) {
            minifiedITUsers.add(serializer.serialize(user, null, null));
        }
        return minifiedITUsers;
    }
    /**
    * First tries to get user using id, if not found gets it using the cid.
    */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject getUser(@PathVariable("id") String id) {
        ITUser user;
        try {
            user = this.itUserService.getUserById(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            user = this.itUserService.loadUser(id);
            if (user == null) {
                throw new UserNotFoundResponse();
            }
        }

        ITUserSerializer serializer = new ITUserSerializer(
                ITUserSerializer.Properties.getAllProperties()
        );
        List<WebsiteView> websites =
                this.userWebsiteService.getWebsitesOrdered(
                        this.userWebsiteService.getWebsites(user)
                );
        List<Membership> memberships = this.addSuperGroupInfo(this.membershipService.getMembershipsByUser(user));
        return serializer.serialize(user, websites, ITUserSerializer.getGroupsAsJson(memberships));
    }

    @RequestMapping(value = "/me", method = RequestMethod.PUT)
    public ResponseEntity<String> editMe(Principal principal, @RequestBody EditITUserRequest request) {
        String cid = principal.getName();
        ITUser user = this.itUserService.loadUser(cid);
        if (user == null) {
            throw new UserNotFoundResponse();
        }
        this.itUserService.editUser(user.getId(), request.getNick(), request.getFirstName(), request.getLastName(),
                request.getEmail(), request.getPhone(), request.getLanguage());
        List<CreateGroupRequest.WebsiteInfo> websiteInfos = request.getWebsites();
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        List<WebsiteInterface> userWebsite = new ArrayList<>(
                this.userWebsiteService.getWebsites(user)
        );
        this.userWebsiteService.addWebsiteToEntity(websiteInfos, userWebsite);
        this.userWebsiteService.addWebsiteToUser(user, websiteURLs);
        return new UserEditedResponse();
    }

    @RequestMapping(value = "/me/avatar", method = RequestMethod.PUT)
    public ResponseEntity<String> editProfileImage(Principal principal, @RequestParam MultipartFile file,
                                                   BindingResult result) {
        String cid = principal.getName();
        ITUser user = this.itUserService.loadUser(cid);
        if (user == null) {
            throw new UserNotFoundResponse();
        }
        try {
            String fileUrl = ImageITUtils.saveImage(file);
            this.itUserService.editProfilePicture(user, fileUrl);
        } catch (IOException e) {
            throw new FileNotSavedException();
        }

        return new EditedProfilePicture();
    }

    @RequestMapping(value = "/me/change_password", method = RequestMethod.PUT)
    public ResponseEntity<String> changePassword(Principal principal, @Valid @RequestBody ChangeUserPassword request,
                                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ITUser user = this.extractUser(principal);
        if (!this.itUserService.passwordMatches(user, request.getOldPassword())) {
            throw new IncorrectCidOrPasswordResponse();
        }
        this.itUserService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    @RequestMapping(value = "/me", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMe(Principal principal, @Valid @RequestBody DeleteMeRequest request,
                                           BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        ITUser user = this.extractUser(principal);
        if (!this.itUserService.passwordMatches(user, request.getPassword())) {
            throw new IncorrectCidOrPasswordResponse();
        }
        this.userWebsiteService.deleteWebsitesConnectedToUser(
                this.itUserService.getUserById(user.getId())
        );
        this.membershipService.removeAllMemberships(user);
        this.itUserService.removeUser(user.getId());
        return new UserDeletedResponse();
    }

    private ITUser extractUser(Principal principal) {
        String cid = principal.getName();
        ITUser user = this.itUserService.loadUser(cid);
        if (user == null) {
            throw new UserNotFoundResponse();
        }
        return user;
    }
    // This should probably do a deep copy instead. But that is not in MVP...
    private List<Membership> addSuperGroupInfo(List<Membership> memberships) {
        List<Membership> membershipsCopy = new ArrayList<>(memberships);
        membershipsCopy.forEach(membership -> membership.setFkitSuperGroups(this.fkitGroupToSuperGroupService
                .getSuperGroups(membership.getId().getFKITGroup())));
        return membershipsCopy;
    }

}
