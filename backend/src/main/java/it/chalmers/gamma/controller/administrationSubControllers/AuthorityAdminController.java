package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.requests.AuthorizationLevelRequest;
import it.chalmers.gamma.requests.AuthorizationRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/authorization")
public class AuthorityAdminController {

    private AuthorityService authorityService;

    private FKITService fkitService;

    private PostService postService;

    private AuthorityLevelService authorityLevelService;

    public AuthorityAdminController(AuthorityService authorityService, FKITService fkitService, PostService postService, AuthorityLevelService authorityLevelService){
        this.authorityService = authorityService;
        this.fkitService = fkitService;
        this.postService = postService;
        this.authorityLevelService = authorityLevelService;
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
        AuthorityLevel level = authorityLevelService.getAuthorityLevel(request.getAuthority());
        if(level == null){
            throw new AuthorityLevelNotFoundResponse();
        }
        authorityService.setAuthorityLevel(group, post, level);
        return new AuthorityAddedResponse();
    }


    @RequestMapping(value = "/user_authorities", method = RequestMethod.GET)
    public ResponseEntity<ITUser> getUsersWithAuthority(){
        // TODO
        return null;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<String> removeAuthorization(@RequestBody AuthorizationRequest request){
        Post post = postService.getPost(UUID.fromString(request.getPost()));
        if(post == null){
            throw new MissingRequiredFieldResponse("post");
        }
        FKITGroup group = fkitService.getGroup(UUID.fromString(request.getGroup()));
        if(group == null){
            throw new MissingRequiredFieldResponse("group");
        }
        authorityService.removeAuthority(group, post);
        return new AuthorityRemovedResponse();
    }
    // EVERYTHING BELOW THIS SHOULD MAYBE BE MOVED TO A DIFFERENT FILE
    @RequestMapping(value = "/level", method = RequestMethod.POST)
    public ResponseEntity<String> addAuthorityLevel(@RequestBody AuthorizationLevelRequest request){
        if(authorityLevelService.authorityLevelExists(request.getAuthorityLevel())){
            throw new AuthorityLevelAlreadyExists();
        }
        authorityLevelService.addAuthorityLevel(request.getAuthorityLevel());
        return new AuthorityLevelAddedResponse();
    }

}
