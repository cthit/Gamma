package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.requests.*;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

@RestController
@RequestMapping(value = "/admin")
public class AdministrationController {

    private ITUserService itUserService;
    private WhitelistService whitelistService;
    private FKITService fkitService;
    private MembershipService membershipService;
    private PostService postService;

    AdministrationController(ITUserService itUserService, WhitelistService whitelistService,
                             FKITService fkitService, MembershipService membershipService, PostService postService){
        this.itUserService = itUserService;
        this.whitelistService = whitelistService;
        this.fkitService = fkitService;
        this.membershipService = membershipService;
        this.postService = postService;
    }
    /**
     * Administrative function that can add user without need for user to add it personally.
     */
    @RequestMapping(value = "/users/new", method = RequestMethod.POST)
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
    @RequestMapping(value = "/groups/edit", method = RequestMethod.POST)
    public ResponseEntity<String> editGroup(@RequestBody CreateGroupRequest request){
        fkitService.editGroup(request.getName(), request.getDescription(), request.getEmail(), request.getGroupType(), request.getFunction());
        return new GroupCreatedResponse();
    }
    @RequestMapping(value = "/groups/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteGroup(@RequestBody DeleteGroupRequest group){
        if(fkitService.groupExists(group.getGroup())){
            return new GroupDoesNotExistResponse();
        }
        fkitService.removeGroup(group.getGroup());
        return new GroupDeletedResponse();
    }
    @RequestMapping(value = "/groups/add_user", method = RequestMethod.POST)
    public ResponseEntity<String> addUserToGroup(@RequestBody AddUserGroupRequest request){
        if(!itUserService.userExists(request.getUser())){
            return new NoCidFoundResponse();
        }
        if(!fkitService.groupExists(request.getGroup())){
            return new GroupDoesNotExistResponse();
        }
        if(!postService.postExists(request.getPost())){
            return new PostDoesNotExistResponse();
        }
        ITUser user = itUserService.loadUser(request.getUser());
        FKITGroup group = fkitService.getGroup(request.getGroup());
        Post post = postService.getPost(request.getPost());
        membershipService.addUserToGroup(group, user, post, request.getUnofficialName(), request.getYear());
        return new UserAddedToGroupResponse();
    }
    @RequestMapping(value = "/groups/add_post")
    public ResponseEntity<String> addOfficialPost(@RequestBody AddPostRequest request){
        if(postService.postExists(request.getPost())){
            return new PostAlreadyExistsResponse();
        }
        postService.addPost(request.getPost());
        return new PostCreatedResponse();
    }


}
