package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.requests.AdminViewCreateITUserRequest;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.requests.CreateITUserRequest;
import it.chalmers.gamma.requests.WhitelistCodeRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.WhitelistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

@RestController
@RequestMapping(value = "/admin")
public class AdministrationController {

    private ITUserService itUserService;
    private WhitelistService whitelistService;
    private FKITService fkitService;

    AdministrationController(ITUserService itUserService, WhitelistService whitelistService, FKITService fkitService){
        this.itUserService = itUserService;
        this.whitelistService = whitelistService;
        this.fkitService = fkitService;
    }
    /**
     * Administrative function that can add user without need for user to add it personally.
     */
    @RequestMapping(value = "/users/add", method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody AdminViewCreateITUserRequest createITUserRequest){
        if (itUserService.userExists(createITUserRequest.getCid())){
            return new UserAlreadyExistsResponse();
        }
        itUserService.createUser(createITUserRequest.getNick(), createITUserRequest.getFirstName(),
                createITUserRequest.getLastName(), createITUserRequest.getCid(), Year.of(createITUserRequest.getAcceptanceYear()),
                createITUserRequest.isUserAgreement(), createITUserRequest.getEmail(), createITUserRequest.getPassword());
        return new UserCreatedResponse();
    }
    /*
    should probably change so multiple users can be added simultaneously
     */
    @RequestMapping(value = "/users/add_whitelisted", method = RequestMethod.POST)
    public ResponseEntity<String> addWhitelistedUser(@RequestBody WhitelistCodeRequest cid) {
        if (whitelistService.isCIDWhiteListed(cid.getCid())) {
            return new CIDAlreadyWhitelistedResponse();
        }
        if (itUserService.userExists(cid.getCid())) {
            return new UserAlreadyExistsResponse();
        }
        whitelistService.addWhiteListedCID(cid.getCid());
        return new UserAddedResponse();
    }

    @RequestMapping(value = "/groups/new", method = RequestMethod.POST)
    public ResponseEntity<String> addNewGroup(@RequestBody CreateGroupRequest createGroupRequest){
        if(fkitService.groupExists(createGroupRequest.getName())){
            return new GroupAlreadyExistsResponse();
        }
        if(createGroupRequest.getName() == null){
            return new MissingRequiredFieldResponse("name");
        }
        if(createGroupRequest.getEmail() == null){
            return new MissingRequiredFieldResponse("email");
        }
        if(createGroupRequest.getFunction() == null){
            return new MissingRequiredFieldResponse("function");
        }
        if(createGroupRequest.getGroupType() == null){
            return new MissingRequiredFieldResponse("groupType");
        }
        fkitService.createGroup(createGroupRequest.getName(), createGroupRequest.getDescription(),
                createGroupRequest.getEmail(), createGroupRequest.getGroupType(), createGroupRequest.getFunction());
        return new GroupCreatedResponse();
    }




}
