package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.requests.AdminChangePasswordRequest;
import it.chalmers.gamma.requests.AdminViewCreateITUserRequest;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.requests.EditITUserRequest;
import it.chalmers.gamma.requests.ResetPasswordFinishRequest;
import it.chalmers.gamma.requests.ResetPasswordRequest;
import it.chalmers.gamma.response.CidNotFoundResponse;
import it.chalmers.gamma.response.CodeOrCidIsWrongResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.PasswordChangedResponse;
import it.chalmers.gamma.response.PasswordResetResponse;
import it.chalmers.gamma.response.UserAlreadyExistsResponse;
import it.chalmers.gamma.response.UserCreatedResponse;
import it.chalmers.gamma.response.UserDeletedResponse;
import it.chalmers.gamma.response.UserEditedResponse;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MailSenderService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PasswordResetService;
import it.chalmers.gamma.service.UserWebsiteService;
import it.chalmers.gamma.util.InputValidationUtils;
import it.chalmers.gamma.util.TokenUtils;
import it.chalmers.gamma.views.WebsiteView;

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
public final class UserAdminController {

    private final ITUserService itUserService;
    private final UserWebsiteService userWebsiteService;
    private final PasswordResetService passwordResetService;
    private final MailSenderService mailSenderService;
    private final MembershipService membershipService;

    public UserAdminController(
            ITUserService itUserService,
            UserWebsiteService userWebsiteService,
            PasswordResetService passwordResetService,
            MailSenderService mailSenderService,
            MembershipService membershipService) {
        this.itUserService = itUserService;
        this.userWebsiteService = userWebsiteService;
        this.passwordResetService = passwordResetService;
        this.mailSenderService = mailSenderService;
        this.membershipService = membershipService;
    }

    @RequestMapping(value = "/{id}/change_password", method = RequestMethod.PUT)
    public ResponseEntity<String> changePassword(
            @Valid @PathVariable("id") String id, BindingResult result,
            @RequestBody AdminChangePasswordRequest request) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new CidNotFoundResponse();
        }
        ITUser user = this.itUserService.getUserById(UUID.fromString(id));
        this.itUserService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    //TODO Make sure that the code to add websites to users actually works
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editUser(@PathVariable("id") String id, @RequestBody EditITUserRequest request) {
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new CidNotFoundResponse();
        }
        this.itUserService.editUser(
                UUID.fromString(id),
                request.getNick(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone(),
                request.getLanguage(),
                request.getAvatarUrl());
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
        if (this.itUserService.userExists(UUID.fromString(id))) {
            throw new CidNotFoundResponse();
        }
        this.userWebsiteService.deleteWebsitesConnectedToUser(
                this.itUserService.getUserById(UUID.fromString(id))
        );
        this.itUserService.removeUser(UUID.fromString(id));
        return new UserDeletedResponse();
    }

    @RequestMapping(value = "/{cid}", method = RequestMethod.GET)
    public JSONObject getUser(@PathVariable("cid") String cid) {
        if (this.itUserService.userExists(UUID.fromString(cid))) {
            throw new CidNotFoundResponse();
        }
        List<ITUserSerializer.Properties> props = ITUserSerializer.Properties.getAllProperties();
        ITUserSerializer serializer = new ITUserSerializer(props);
        ITUser user = this.itUserService.getUserById(UUID.fromString(cid));
        List<WebsiteView> websiteViews =
                this.userWebsiteService.getWebsitesOrdered(
                        this.userWebsiteService.getWebsites(user)
                );
        return serializer.serialize(user, websiteViews,
                ITUserSerializer.getGroupsAsJson(this.membershipService.getMembershipsByUser(user)));
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<JSONObject> getAllUsers() {
        List<ITUserSerializer.Properties> props = ITUserSerializer.Properties.getAllProperties();
        ITUserSerializer serializer = new ITUserSerializer(props);
        List<ITUser> users = this.itUserService.loadAllUsers();
        List<JSONObject> userViewList = new ArrayList<>();
        for (ITUser user : users) {
            List<WebsiteView> websiteViews =
                    this.userWebsiteService.getWebsitesOrdered(
                            this.userWebsiteService.getWebsites(user)
                    );
            JSONObject userView = serializer.serialize(user, websiteViews,
                    ITUserSerializer.getGroupsAsJson(this.membershipService.getMembershipsByUser(user)));
            userViewList.add(userView);

        }
        return userViewList;
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

    //TODO MOVE THIS TO ITUSERCONTROLLER
    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public ResponseEntity<String> resetPasswordRequest(
            @Valid @RequestBody ResetPasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.itUserService.userExists(request.getCid())) {
            throw new CidNotFoundResponse();
        }
        ITUser user = this.itUserService.loadUser(request.getCid());
        String token = TokenUtils.generateToken();
        if (this.passwordResetService.userHasActiveReset(user)) {
            this.passwordResetService.editToken(user, token);
        } else {
            this.passwordResetService.addToken(user, token);
        }
        sendMail(user, token);
        return new PasswordResetResponse();
    }

    //TODO MOVE THIS TO ITUSERCONTROLLER
    @RequestMapping(value = "/reset_password/finish", method = RequestMethod.PUT)
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordFinishRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.itUserService.userExists(request.getCid())) {
            throw new CidNotFoundResponse();
        }
        ITUser user = this.itUserService.loadUser(request.getCid());
        if (!this.passwordResetService.userHasActiveReset(user)
                || !this.passwordResetService.tokenMatchesUser(user, request.getToken())) {
            throw new CodeOrCidIsWrongResponse();
        }
        this.itUserService.setPassword(user, request.getPassword());
        this.passwordResetService.removeToken(user);
        return new PasswordChangedResponse();
    }
    // TODO Make sure that an URL is added to the email
    private void sendMail(ITUser user, String token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token + "URL : ";
        this.mailSenderService.sendMail(user.getCid(), subject, message);
    }

}
