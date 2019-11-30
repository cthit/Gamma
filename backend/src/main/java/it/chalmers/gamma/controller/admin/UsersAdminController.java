package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.requests.AdminChangePasswordRequest;
import it.chalmers.gamma.requests.AdminViewCreateITUserRequest;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.requests.EditITUserRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.PasswordChangedResponse;
import it.chalmers.gamma.response.UserAlreadyExistsResponse;
import it.chalmers.gamma.response.UserCreatedResponse;
import it.chalmers.gamma.response.UserDeletedResponse;
import it.chalmers.gamma.response.UserEditedResponse;
import it.chalmers.gamma.response.UserNotFoundResponse;
import it.chalmers.gamma.response.user.GetITUsersResponse;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.UserWebsiteService;
import it.chalmers.gamma.util.InputValidationUtils;
import it.chalmers.gamma.views.WebsiteDTO;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.ExcessiveImports"})
@RestController
@RequestMapping("/admin/users")
public final class UsersAdminController {

    private final ITUserService itUserService;
    private final UserWebsiteService userWebsiteService;


    private final MembershipService membershipService;

    public UsersAdminController(
            ITUserService itUserService,
            UserWebsiteService userWebsiteService,
            MembershipService membershipService) {
        this.itUserService = itUserService;
        this.userWebsiteService = userWebsiteService;

        this.membershipService = membershipService;
    }

    @RequestMapping(value = "/{id}/change_password", method = RequestMethod.PUT)
    public ResponseEntity<String> changePassword(
            @PathVariable("id") String id,
            @Valid @RequestBody AdminChangePasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        ITUser user = this.itUserService.getUserById(UUID.fromString(id));
        this.itUserService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    //TODO Make sure that the code to add websites to users actually works
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editUser(@PathVariable("id") String id,
                                           @Valid @RequestBody EditITUserRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        this.itUserService.editUser(
                UUID.fromString(id),
                request.getNick(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getLanguage());
        // Below handles adding websites.
        ITUser user = this.itUserService.getUserById(UUID.fromString(id));
        List<CreateGroupRequest.WebsiteInfo> websiteInfos = request.getWebsites();
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        List<WebsiteInterface> userWebsite = new ArrayList<>(
                this.userWebsiteService.getWebsites(user)
        );
        this.userWebsiteService.addWebsiteToEntity(websiteInfos, userWebsite);
        this.userWebsiteService.addWebsiteToUser(user, websiteURLs);
        return new UserEditedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        ITUser user = this.itUserService.getUserById(UUID.fromString(id));
        this.userWebsiteService.deleteWebsitesConnectedToUser(user);
        this.membershipService.removeAllMemberships(user);
        this.itUserService.removeUser(user.getId());
        return new UserDeletedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject getUser(@PathVariable("id") String id) {
        try {
            if (!this.itUserService.userExists(UUID.fromString(id))) {
                throw new UserNotFoundResponse();
            }
        } catch (IllegalArgumentException e) {
            throw new UserNotFoundResponse();
        }
        List<ITUserSerializer.Properties> props = ITUserSerializer.Properties.getAllProperties();
        ITUserDTO user = this.itUserService.getUserById(UUID.fromString(id));
        List<WebsiteDTO> websiteViews =
                this.userWebsiteService.getWebsitesOrdered(
                        this.userWebsiteService.getWebsites(user)
                );
        return serializer.serialize(user, websiteViews,
                ITUserSerializer.getGroupsAsJson(this.membershipService.getMembershipsByUser(user)));
    }

    @RequestMapping(method = RequestMethod.GET)
    public GetITUsersResponse getAllUsers() {
//        List<ITUserSerializer.Properties> props = ITUserSerializer.Properties.getAllProperties();
//        ITUserSerializer serializer = new ITUserSerializer(props);
//        List<ITUser> users = this.itUserService.loadAllUsers();
//        List<JSONObject> userViewList = new ArrayList<>();
//        for (ITUser user : users) {
//            List<WebsiteDTO> websiteViews =
//                    this.userWebsiteService.getWebsitesOrdered(
//                            this.userWebsiteService.getWebsites(user)
//                    );
//            JSONObject userView = serializer.serialize(user, websiteViews,
//                    ITUserSerializer.getGroupsAsJson(this.membershipService.getMembershipsByUser(user)));
//            userViewList.add(userView);
//
//        }
        return this.itUserService.loadAllUsers();
    }

    /**
     * Administrative function that can add user without need for user to add it personally.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addUser(
            @Valid @RequestBody AdminViewCreateITUserRequest createITUserRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.itUserService.userExists(createITUserRequest.getCid())) {
            throw new UserAlreadyExistsResponse();
        }
        this.itUserService.createUser(
                createITUserRequest.getNick(),
                createITUserRequest.getFirstName(),
                createITUserRequest.getLastName(),
                createITUserRequest.getCid(),
                Year.of(createITUserRequest.getAcceptanceYear()),
                createITUserRequest.isUserAgreement(),
                createITUserRequest.getEmail(),
                createITUserRequest.getPassword()
        );
        return new UserCreatedResponse();
    }



}
