package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Whitelist;
import it.chalmers.gamma.requests.*;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping(value = "/admin")
public class AdministrationController {

    private ITUserService itUserService;
    private WhitelistService whitelistService;
    private FKITService fkitService;
    private MembershipService membershipService;
    private PostService postService;

    AdministrationController(ITUserService itUserService, WhitelistService whitelistService,
                             FKITService fkitService, MembershipService membershipService, PostService postService) {
        this.itUserService = itUserService;
        this.whitelistService = whitelistService;
        this.fkitService = fkitService;
        this.membershipService = membershipService;
        this.postService = postService;
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public ResponseEntity<List<FKITGroup>> getGroups(){
        return new GroupsResponse(fkitService.getGroups());
    }

    @RequestMapping(value = "/groups/new", method = RequestMethod.POST)
    public ResponseEntity<String> addNewGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        if (fkitService.groupExists(createGroupRequest.getName())) {
            return new GroupAlreadyExistsResponse();
        }
        if (createGroupRequest.getName() == null) {
            return new MissingRequiredFieldResponse("name");
        }
        if (createGroupRequest.getEmail() == null) {
            return new MissingRequiredFieldResponse("email");
        }
        if (createGroupRequest.getFunc() == null) {
            return new MissingRequiredFieldResponse("function");
        }
        if (createGroupRequest.getType() == null) {
            return new MissingRequiredFieldResponse("groupType");
        }
        fkitService.createGroup(createGroupRequest.getName(), createGroupRequest.getDescription(),
                createGroupRequest.getEmail(), createGroupRequest.getType(), createGroupRequest.getFunc());
        return new GroupCreatedResponse();
    }

    @RequestMapping(value = "/groups/{group}", method = RequestMethod.POST)
    public ResponseEntity<String> editGroup(@RequestBody CreateGroupRequest request, @PathVariable("group") String group) {
        fkitService.editGroup(group, request.getDescription(), request.getEmail(), request.getType(), request.getFunc());
        return new GroupCreatedResponse();
    }

    @RequestMapping(value = "/groups/{group}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteGroup(@PathVariable("group") String group) {
        if (fkitService.groupExists(group)) {
            return new GroupDoesNotExistResponse();
        }
        fkitService.removeGroup(group);
        return new GroupDeletedResponse();
    }

    @RequestMapping(value = "/groups/{group}/members", method = RequestMethod.POST)
    public ResponseEntity<String> addUserToGroup(@RequestBody AddUserGroupRequest request, @PathVariable("group") String group) {
        if (!itUserService.userExists(request.getUser())) {
            return new NoCidFoundResponse();
        }
        if (!postService.postExists(request.getPost())) {
            return new PostDoesNotExistResponse();
        }
        ITUser user = itUserService.loadUser(request.getUser());
        FKITGroup fkitGroup = fkitService.getGroup(group);
        Post post = postService.getPost(request.getPost());
        membershipService.addUserToGroup(fkitGroup, user, post, request.getUnofficialName(), request.getYear());
        return new UserAddedToGroupResponse();
    }

    @RequestMapping(value = "/groups/posts", method = RequestMethod.POST)
    public ResponseEntity<String> addOfficialPost(@RequestBody AddPostRequest request) {
        if (postService.postExists(request.getPost().getSv())) {
            return new PostAlreadyExistsResponse();
        }
        postService.addPost(request.getPost());
        return new PostCreatedResponse();
    }
    @RequestMapping(value = "/groups/posts/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editPost(@RequestBody AddPostRequest request, @PathVariable("id") String id){
        Post post = postService.getPostById(id);
        if(post == null){
            return new MissingRequiredFieldResponse("post");
        }
        postService.editPost(post, request.getPost());
        return new EditedPostResponse();
    }
    @RequestMapping(value = "/groups/posts/{id}", method = RequestMethod.GET)
    public ResponseEntity<Post> getPost(@PathVariable("id") String id){
        Post post = postService.getPostById(id);
        return new GetPostResponse(post);
    }


    @RequestMapping(value = "/groups/posts", method = RequestMethod.GET)
    public ResponseEntity<List<Post>> getPosts() {
        return new GetMultiplePostsResponse(postService.getAllPosts());
    }

    @RequestMapping(value = "/users/{cid}", method = RequestMethod.POST)
    public ResponseEntity<String> editUser(@PathVariable("cid") String cid, @RequestBody EditITUserRequest request) {
        itUserService.editUser(cid, request.getNick(), request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhone(), request.getLanguage(), request.getAvatarUrl());
        return new UserEditedResponse();
    }

    @RequestMapping(value = "/users/{cid}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable("cid") String cid){
        itUserService.removeUser(cid);
        return new UserDeletedResponse();
    }

    /**
     * Administrative function that can add user without need for user to add it personally.
     */
    @RequestMapping(value = "/users/new", method = RequestMethod.POST)
    public ResponseEntity<String> addUser(@RequestBody AdminViewCreateITUserRequest createITUserRequest) {
        if (itUserService.userExists(createITUserRequest.getCid())) {
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
    @RequestMapping(value = "/users/whitelist", method = RequestMethod.POST)
    public ResponseEntity<String> addWhitelistedUser(@RequestBody WhitelistCodeRequest cid) {
        if (whitelistService.isCIDWhiteListed(cid.getCid())) {
            return new CIDAlreadyWhitelistedResponse();
        }
        if (itUserService.userExists(cid.getCid())) {
            return new UserAlreadyExistsResponse();
        }
        whitelistService.addWhiteListedCID(cid.getCid());
        return new WhitelistAddedResponse();
    }
    @RequestMapping(value = "/users/whitelist/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editWhitelist(@RequestBody WhitelistCodeRequest request, @PathVariable("id") String id){
        Whitelist oldWhitelist = whitelistService.getWhitelistById(id);
        if(!whitelistService.isCIDWhiteListed(oldWhitelist.getCid())){
            return new NoCidFoundResponse();
        }
        if(whitelistService.isCIDWhiteListed(request.getCid())){
            return new CIDAlreadyWhitelistedResponse();
        }
        if(request.getCid() == null){
            return new MissingRequiredFieldResponse("cid");
        }
        whitelistService.editWhitelist(oldWhitelist, request.getCid());
        return new EditedWhitelistResponse();
    }
    @RequestMapping(value = "/users/whitelist/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeWhitelist(@PathVariable("id") String id){
        Whitelist whitelist = whitelistService.getWhitelistById(id);
        if(whitelist == null){
            return new NoCidFoundResponse();
        }
        whitelistService.removeWhiteListedCID(whitelist.getCid());
        return new UserDeletedResponse();
    }

    @RequestMapping(value = "/users/whitelist", method = RequestMethod.GET)
    public ResponseEntity<List<Whitelist>> getAllWhiteList(){
        return new GetWhitelistedResponse(whitelistService.getAllWhitelist());
    }

}
