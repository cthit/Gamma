package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.requests.AuthorizationLevelRequest;
import it.chalmers.gamma.requests.AuthorizationRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.*;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/authorization")
public class AuthorityAdminController {

    private final AuthorityService authorityService;

    private final FKITService fkitService;

    private final PostService postService;

    private final AuthorityLevelService authorityLevelService;

    private final MembershipService membershipService;

    public AuthorityAdminController(AuthorityService authorityService, FKITService fkitService,
                                    PostService postService, AuthorityLevelService authorityLevelService,
                                    MembershipService membershipService){
        this.authorityService = authorityService;
        this.fkitService = fkitService;
        this.postService = postService;
        this.authorityLevelService = authorityLevelService;
        this.membershipService = membershipService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addAuthority(@RequestBody AuthorizationRequest request){
        Post post = postService.getPost(UUID.fromString(request.getPost()));
        if(post == null){
            throw new PostDoesNotExistResponse();
        }
        FKITGroup group = fkitService.getGroup(UUID.fromString(request.getGroup()));
        if(group == null){
            throw new GroupDoesNotExistResponse();
        }
        AuthorityLevel level = authorityLevelService.getAuthorityLevel(UUID.fromString(request.getAuthority()));
        if(level == null){
            throw new AuthorityLevelNotFoundResponse();
        }
        authorityService.setAuthorityLevel(group, post, level);
        return new AuthorityAddedResponse();
    }


    @RequestMapping(value = "/user_authorities/{authorityLevel}", method = RequestMethod.GET)
    public List<JSONObject> getUsersWithAuthority(@PathVariable("authorityLevel") String level){
        System.out.println(level);
        AuthorityLevel authorityLevel = authorityLevelService.getAuthorityLevel(UUID.fromString(level));
        List<Authority> authorities = authorityService.getAllAuthoritiesWithAuthorityLevel(authorityLevel);
        ITUserSerializer serializer = new ITUserSerializer(ITUserSerializer.Properties.getAllProperties());
        List<JSONObject> users = new ArrayList<>();
        for(Authority authority : authorities){
            FKITGroup group = authority.getId().getFkitGroup();
            Post post = authority.getId().getPost();
            List<ITUser> userDatas = membershipService.getUserByGroupAndPost(group, post);
            for(ITUser user : userDatas){
                users.add(serializer.serialize(user, null));
            }
        }
        return users;
    }

    @RequestMapping(method = RequestMethod.DELETE)  // TODO change this to use URL id instead
    public ResponseEntity<String> removeAuthorization(@RequestBody AuthorizationRequest request){
        Post post = postService.getPost(UUID.fromString(request.getPost()));
        if(post == null){
            throw new PostDoesNotExistResponse();
        }
        FKITGroup group = fkitService.getGroup(UUID.fromString(request.getGroup()));
        if(group == null){
            throw new GroupDoesNotExistResponse();
        }
        authorityService.removeAuthority(group, post);
        return new AuthorityRemovedResponse();
    }
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Authority>> getAllAuthorities(){
        List<Authority> authorities = authorityService.getAllAuthorities();
        return new GetAllAuthoritiesResponse(authorities);
    }
    // EVERYTHING BELOW THIS SHOULD MAYBE BE MOVED TO A DIFFERENT FILE
    @RequestMapping(value = "/level", method = RequestMethod.POST)
    public ResponseEntity<String> addAuthorityLevel(@RequestBody AuthorizationLevelRequest request){
        if(request.getAuthorityLevel() == null){
            throw new MissingRequiredFieldResponse("AuthorityLevel");
        }
        if(authorityLevelService.authorityLevelExists(request.getAuthorityLevel())){
            throw new AuthorityLevelAlreadyExists();
        }
        authorityLevelService.addAuthorityLevel(request.getAuthorityLevel());
        return new AuthorityLevelAddedResponse();
    }

}
