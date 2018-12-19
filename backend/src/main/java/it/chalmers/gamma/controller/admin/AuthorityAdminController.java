package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.requests.AuthorizationLevelRequest;
import it.chalmers.gamma.requests.AuthorizationRequest;
import it.chalmers.gamma.response.AuthorityAddedResponse;
import it.chalmers.gamma.response.AuthorityLevelAddedResponse;
import it.chalmers.gamma.response.AuthorityLevelAlreadyExists;
import it.chalmers.gamma.response.AuthorityLevelNotFoundResponse;
import it.chalmers.gamma.response.AuthorityLevelRemovedResponse;
import it.chalmers.gamma.response.AuthorityNotFoundResponse;
import it.chalmers.gamma.response.AuthorityRemovedResponse;
import it.chalmers.gamma.response.GetAllAuthoritiesResponse;
import it.chalmers.gamma.response.GetAllAuthorityLevelsResponse;
import it.chalmers.gamma.response.GetAuthorityResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.MissingRequiredFieldResponse;
import it.chalmers.gamma.response.PostDoesNotExistResponse;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.PostService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.AvoidDuplicateLiterals"})
@RestController
@RequestMapping("/admin/authority")
public final class AuthorityAdminController {

    private final AuthorityService authorityService;
    private final PostService postService;
    private final AuthorityLevelService authorityLevelService;
    private final FKITSuperGroupService fkitSuperGroupService;

    public AuthorityAdminController(AuthorityService authorityService,
                                     PostService postService,
                                     AuthorityLevelService authorityLevelService,
                                     FKITSuperGroupService fkitSuperGroupService) {
        this.authorityService = authorityService;
        this.postService = postService;
        this.authorityLevelService = authorityLevelService;
        this.fkitSuperGroupService = fkitSuperGroupService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addAuthority(@RequestBody AuthorizationRequest request) {
        Post post = this.postService.getPost(UUID.fromString(request.getPost()));
        if (post == null) {
            throw new PostDoesNotExistResponse();
        }
        FKITSuperGroup group = this.fkitSuperGroupService.getGroup(UUID.fromString(request.getSuperGroup()));
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        AuthorityLevel level =
                this.authorityLevelService.getAuthorityLevel(
                        UUID.fromString(request.getAuthority())
                );
        if (level == null) {
            throw new AuthorityLevelNotFoundResponse();
        }
        this.authorityService.setAuthorityLevel(group, post, level);
        return new AuthorityAddedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeAuthorization(@PathVariable("id") String id) {
        this.authorityService.removeAuthority(UUID.fromString(id));
        return new AuthorityRemovedResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Authority>> getAllAuthorities() {
        List<Authority> authorities = this.authorityService.getAllAuthorities();
        return new GetAllAuthoritiesResponse(authorities);
    }

    // BELOW THIS SHOULD MAYBE BE MOVED TO A DIFFERENT FILE
    @RequestMapping(value = "/level", method = RequestMethod.POST)
    public ResponseEntity<String> addAuthorityLevel(@RequestBody AuthorizationLevelRequest request) {
        if (request.getAuthorityLevel() == null) {
            throw new MissingRequiredFieldResponse("authorityLevel");
        }
        if (this.authorityLevelService.authorityLevelExists(request.getAuthorityLevel())) {
            throw new AuthorityLevelAlreadyExists();
        }
        this.authorityLevelService.addAuthorityLevel(request.getAuthorityLevel());
        return new AuthorityLevelAddedResponse();
    }

    @RequestMapping(value = "/level", method = RequestMethod.GET)
    public ResponseEntity<List<AuthorityLevel>> getAllAuthorityLevels() {
        List<AuthorityLevel> authorityLevels = this.authorityLevelService.getAllAuthorityLevels();
        return new GetAllAuthorityLevelsResponse(authorityLevels);
    }

    @RequestMapping(value = "/level/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeAuthorityLevel(@PathVariable("id") String id) {
        this.authorityLevelService.removeAuthorityLevel(UUID.fromString(id));
        return new AuthorityLevelRemovedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Authority> getAuthority(@PathVariable("id") String id) {
        Authority authority = this.authorityService.getAuthority(UUID.fromString(id));
        if (authority == null) {
            throw new AuthorityNotFoundResponse();
        }
        return new GetAuthorityResponse(authority);
    }

}

