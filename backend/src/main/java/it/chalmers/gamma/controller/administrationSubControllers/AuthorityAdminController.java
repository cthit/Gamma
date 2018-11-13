package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.requests.AuthorizationRequest;
import it.chalmers.gamma.response.AuthorityAddedResponse;
import it.chalmers.gamma.response.AuthorityRemovedResponse;
import it.chalmers.gamma.response.MissingRequiredFieldResponse;
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

    public AuthorityAdminController(AuthorityService authorityService, FKITService fkitService, PostService postService){
        this.authorityService = authorityService;
        this.fkitService = fkitService;
        this.postService = postService;

    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addAuthority(@RequestBody AuthorizationRequest request){
        Post post = postService.getPost(UUID.fromString(request.getPost()));
        if(post == null){
            throw new MissingRequiredFieldResponse("post");
        }
        FKITGroup group = fkitService.getGroup(UUID.fromString(request.getGroup()));
        if(group == null){
            throw new MissingRequiredFieldResponse("group");
        }
        authorityService.setAuthorityLevel(group, post, Authority.Authorities.valueOf(request.getAuthority()));
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

}
