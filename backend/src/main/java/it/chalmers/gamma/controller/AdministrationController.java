package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.requests.*;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.*;
import org.h2.engine.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/admin")
public class AdministrationController {

    private ITUserService itUserService;
    private WhitelistService whitelistService;
    private FKITService fkitService;
    private MembershipService membershipService;
    private PostService postService;
    private WebsiteService websiteService;
    private GroupWebsiteService groupWebsiteService;
    private ActivationCodeService activationCodeService;
    private UserWebsiteService userWebsiteService;

    AdministrationController(ITUserService itUserService, WhitelistService whitelistService,
                             FKITService fkitService, MembershipService membershipService, PostService postService,
                             WebsiteService websiteService, GroupWebsiteService groupWebsiteService, ActivationCodeService activationCodeService,
                             UserWebsiteService userWebsiteService) {
        this.itUserService = itUserService;
        this.whitelistService = whitelistService;
        this.fkitService = fkitService;
        this.membershipService = membershipService;
        this.postService = postService;
        this.websiteService = websiteService;
        this.groupWebsiteService = groupWebsiteService;
        this.activationCodeService = activationCodeService;
        this.userWebsiteService = userWebsiteService;
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public ResponseEntity<List<FKITGroup>> getGroups(){
        return new GroupsResponse(fkitService.getGroups());
    }

    @RequestMapping(value = "/groups/{group}/minified", method = RequestMethod.GET)
    public ResponseEntity<FKITGroup.FKITGroupView> getGroupMinified(@PathVariable("group") String groupId){
        String[] properties = {"name", "enFunc", "svFunc", "id", "type"};
        FKITGroup group = fkitService.getGroup(groupId);
        if(group == null){
            return new GetGroupResponse(null);
        }
        List<String> props = new ArrayList<>(Arrays.asList(properties));
        FKITGroup.FKITGroupView groupView = group.getView(props);
        return new GetGroupResponse(groupView);
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
        FKITGroup group = fkitService.createGroup(createGroupRequest.getName(), createGroupRequest.getDescription(),
                createGroupRequest.getEmail(), createGroupRequest.getType(), createGroupRequest.getFunc(), createGroupRequest.getAvatarURL());
        List<CreateGroupRequest.WebsiteInfo> websites = createGroupRequest.getWebsites();
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        for(CreateGroupRequest.WebsiteInfo websiteInfo : websites){
            Website website = websiteService.getWebsite(websiteInfo.getWebsite());
            WebsiteURL websiteURL = new WebsiteURL();
            websiteURL.setWebsite(website);
            websiteURL.setUrl(websiteInfo.getUrl());
            websiteURLs.add(websiteURL);
        }
        groupWebsiteService.addGroupWebsites(group, websiteURLs);
        return new GroupCreatedResponse();
    }

    @RequestMapping(value = "/groups/{group}", method = RequestMethod.PUT)
    public ResponseEntity<String> editGroup(@RequestBody CreateGroupRequest request, @PathVariable("group") String group) {
        fkitService.editGroup(group, request.getDescription(), request.getEmail(), request.getType(), request.getFunc(), request.getAvatarURL());
        return new GroupEditedResponse();
    }

    @RequestMapping(value = "/groups/{group}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteGroup(@PathVariable("group") String group) {
        if (!fkitService.groupExists(group)) {
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
    @RequestMapping(value = "posts/{postId}/usage")
    public ResponseEntity<List<FKITGroup.FKITGroupView>> getPostUsages(@PathVariable("postId") String postId){
        String[] properties = {"id", "name", "prettyName"};
        List<String> props = new ArrayList<>(Arrays.asList(properties));
        Post post = postService.getPostById(postId);
        List<UUID> groups = membershipService.getGroupsWithPost(post);
        List<FKITGroup.FKITGroupView> groupAndUser = new ArrayList<>();
        for(UUID groupId : groups) {
            FKITGroup group = fkitService.getGroupById(groupId);
            FKITGroup.FKITGroupView groupView = group.getView(props);
            List<ITUser> users = new ArrayList<>();
            List<UUID> userIDs = membershipService.getUserIdsByGroupAndPost(group, post);
            for(UUID userId: userIDs){
                users.add(itUserService.getUserById(userId));
            }
            groupView.setUsers(users);
            groupAndUser.add(groupView);
        }
        return new PostUsageResponse(groupAndUser);
    }

    @RequestMapping(value = "/users/{cid}", method = RequestMethod.PUT)
    public ResponseEntity<String> editUser(@PathVariable("cid") String cid, @RequestBody EditITUserRequest request) {
        itUserService.editUser(cid, request.getNick(), request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhone(), request.getLanguage(), request.getAvatarUrl());
        ITUser user = itUserService.loadUser(cid);
        List<CreateGroupRequest.WebsiteInfo> websiteInfos = request.getWebsite();
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        List<UserWebsite> userWebsite = userWebsiteService.getWebsites(user);
        for(CreateGroupRequest.WebsiteInfo websiteInfo : websiteInfos){
            boolean websiteExists = false;
            Website website = websiteService.getWebsite(websiteInfo.getWebsite());
            WebsiteURL websiteURL = null;
            for(UserWebsite duplicateCheck : userWebsite){
                if(duplicateCheck.getWebsite().getWebsite().equals(website)) {
                    websiteURL = userWebsiteService.getUserWebsiteByWebsite(website).getWebsite();
                    websiteExists = true;
                    break;
                }
            }
            if(!websiteExists) {
                websiteURL = new WebsiteURL();
            }
            websiteURL.setWebsite(website);
            websiteURL.setUrl(websiteInfo.getUrl());
            websiteURLs.add(websiteURL);
        }
        userWebsiteService.addWebsiteToUser(user, websiteURLs);
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
    public ResponseEntity<String> addWhitelistedUser(@RequestBody AddListOfWhitelistedRequest request) {
        List<String> cids = request.getCids();
        for(String cid : cids) {
            if (whitelistService.isCIDWhiteListed(cid)) {
                return new CIDAlreadyWhitelistedResponse();
            }
            if (itUserService.userExists(cid)) {
                return new UserAlreadyExistsResponse();
            }
            whitelistService.addWhiteListedCID(cid);
        }
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
    @RequestMapping(value = "/websites", method = RequestMethod.POST)
    public ResponseEntity<String> addWebsite(@RequestBody CreateWebsiteRequest request){
        if(request.getName() == null){
            return new MissingRequiredFieldResponse("name");
        }
        websiteService.addPossibleWebsite(request.getName());
        return new WebsiteAddedResponse();
    }
    @RequestMapping(value = "/websites/{id}", method = RequestMethod.GET)
    public ResponseEntity<Website> getWebsite(@PathVariable("id") String id){
        return new GetWebsiteResponse(websiteService.getWebsiteById(id));
    }
    @RequestMapping(value = "/websites/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editWebsite(@PathVariable("id") String id, @RequestBody CreateWebsiteRequest request){
        Website website = websiteService.getWebsiteById(id);
        if(website == null){
            return new WebsiteNotFoundResponse();
        }
        websiteService.editWebsite(website, request.getName());
        return new EditedWebsiteResponse();
    }
    @RequestMapping(value = "/websites/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteWebsite(@PathVariable("id") String id){
        websiteService.deleteWebsite(id);
        return new WebsiteDeletedResponse();
    }
    @RequestMapping(value = "/websites", method = RequestMethod.GET)
    public ResponseEntity<List<Website>> getAllWebsites(){
        return new GetAllWebsitesResponse(websiteService.getAllWebsites());
    }

    @RequestMapping(value = "/activation_codes", method = RequestMethod.GET)
    public ResponseEntity<List<ActivationCode>> getAllActivationCodes(){
        return new GetAllActivationCodesResponse(activationCodeService.getAllActivationCodes());
    }

}
