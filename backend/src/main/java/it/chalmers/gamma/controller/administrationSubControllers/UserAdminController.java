package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.requests.*;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.*;
import it.chalmers.gamma.util.TokenGenerator;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
public class UserAdminController {

    private ITUserService itUserService;
    private UserWebsiteService userWebsiteService;
    private PasswordResetService passwordResetService;

    private UserAdminController(ITUserService itUserService, UserWebsiteService userWebsiteService, WebsiteService websiteService, PasswordResetService passwordResetService){
        this.itUserService = itUserService;
        this.userWebsiteService = userWebsiteService;
        this.passwordResetService = passwordResetService;
    }

    @RequestMapping(value = "/{id}/change_password", method = RequestMethod.PUT)
    public ResponseEntity<String> changePassword(@PathVariable("id") String id, @RequestBody AdminChangePasswordRequest request){
        if(!itUserService.userExists(UUID.fromString(id))){
            throw new CidNotFoundResponse();
        }
        ITUser user = itUserService.getUserById(UUID.fromString(id));
        itUserService.setPassword(user, request.getPassword());
        return new PasswordChangedResponse();
    }

    //TODO Make sure that the code to add websites to users actually works
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editUser(@PathVariable("id") String id, @RequestBody EditITUserRequest request) {
        if(!itUserService.editUser(UUID.fromString(id), request.getNick(), request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhone(), request.getLanguage(), request.getAvatarUrl())){
            throw new CidNotFoundResponse();
        }
        ITUser user = itUserService.getUserById(UUID.fromString(id));
        List<CreateGroupRequest.WebsiteInfo> websiteInfos = request.getWebsites();
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        List<WebsiteInterface> userWebsite = new ArrayList<>(userWebsiteService.getWebsites(user));
        userWebsiteService.addWebsiteToEntity(websiteInfos, userWebsite);
        userWebsiteService.addWebsiteToUser(user, websiteURLs);
        return new UserEditedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id){
        userWebsiteService.deleteWebsitesConnectedToUser(itUserService.getUserById(UUID.fromString(id)));
        itUserService.removeUser(UUID.fromString(id));
        return new UserDeletedResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<JSONObject> getAllUsers(){
        List<ITUserSerializer.Properties> props = ITUserSerializer.Properties.getAllProperties();
        ITUserSerializer serializer = new ITUserSerializer(props);
        List<ITUser> users = itUserService.loadAllUsers();
        List<JSONObject> userViewList = new ArrayList<>();
        for(ITUser user : users){
            List<EntityWebsiteService.WebsiteView> websiteViews = userWebsiteService.getWebsitesOrdered(userWebsiteService.getWebsites(user));
            JSONObject userView = serializer.serialize(user, websiteViews);
            userViewList.add(userView);

        }
        return userViewList;
    }

    /**
     * Administrative function that can add user without need for user to add it personally.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody AdminViewCreateITUserRequest createITUserRequest) {
        if (itUserService.userExists(createITUserRequest.getCid())) {
            throw new UserAlreadyExistsResponse();
        }
        itUserService.createUser(createITUserRequest.getNick(), createITUserRequest.getFirstName(),
                createITUserRequest.getLastName(), createITUserRequest.getCid(), Year.of(createITUserRequest.getAcceptanceYear()),
                createITUserRequest.isUserAgreement(), createITUserRequest.getEmail(), createITUserRequest.getPassword());
        return new UserCreatedResponse();
    }
    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public ResponseEntity<String> resetPasswordRequest(@RequestBody ResetPasswordRequest request){
        if(!itUserService.userExists(UUID.fromString(request.getId()))){
            throw new CidNotFoundResponse();
        }
        ITUser user = itUserService.getUserById(UUID.fromString(request.getId()));
        String token = TokenGenerator.generateToken();
        if(passwordResetService.userHasActiveReset(user)){
            passwordResetService.editToken(user, token);
        }
        else {
            passwordResetService.addToken(user, token);
        }
  //      try {
       //     mailSenderService.sendPasswordReset(user, token);
      //  } catch (MessagingException e) {
        //    e.printStackTrace();
       // }
        return new PasswordResetResponse();
    }
    @RequestMapping(value = "/reset_password/finish", method = RequestMethod.PUT)
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordFinishRequest request){
        if(!itUserService.userExists(request.getCid())){
            throw new CidNotFoundResponse();
        }
        ITUser user = itUserService.loadUser(request.getCid());
        if(!passwordResetService.userHasActiveReset(user) || !passwordResetService.tokenMatchesUser(user, request.getToken())){
            throw new CodeOrCidIsWrongResponse();
        }
        itUserService.setPassword(user, request.getPassword());
        passwordResetService.removeToken(user);
        return new PasswordChangedResponse();
    }

}
