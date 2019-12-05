package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.Authority;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.requests.AuthorizationLevelRequest;
import it.chalmers.gamma.requests.AuthorizationRequest;
import it.chalmers.gamma.response.authority.AuthorityAddedResponse;
import it.chalmers.gamma.response.authority.AuthorityLevelAddedResponse;
import it.chalmers.gamma.response.authority.AuthorityLevelAlreadyExists;
import it.chalmers.gamma.response.authority.AuthorityLevelRemovedResponse;
import it.chalmers.gamma.response.authority.AuthorityNotFoundResponse;
import it.chalmers.gamma.response.authority.AuthorityRemovedResponse;
import it.chalmers.gamma.response.authority.GetAllAuthoritiesResponse;
import it.chalmers.gamma.response.authority.GetAllAuthorityLevelsResponse;
import it.chalmers.gamma.response.authority.GetAllAuthorityLevelsResponse.GetAllAuthorityLevelsResponseObject;
import it.chalmers.gamma.response.authority.GetAuthorityResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.authority.GetAuthorityResponse.GetAuthorityResponseObject;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.PostService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public AuthorityAddedResponse addAuthority(@Valid @RequestBody AuthorizationRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        PostDTO post = this.postService.getPostDTO(request.getPost());
        FKITSuperGroupDTO group = this.fkitSuperGroupService.getGroupDTO(request.getSuperGroup());
        AuthorityLevelDTO level = this.authorityLevelService.getAuthorityLevelDTO(request.getAuthority());
        this.authorityService.setAuthorityLevel(group, post, level);
        return new AuthorityAddedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public AuthorityRemovedResponse removeAuthority(@PathVariable("id") String id) {
        if (!this.authorityService.authorityExists(id)) {
            throw new AuthorityNotFoundResponse();
        }
        this.authorityService.removeAuthority(UUID.fromString(id)); // TODO move check to service?
        return new AuthorityRemovedResponse();
    }

    @RequestMapping(method = RequestMethod.GET)
    public GetAllAuthoritiesResponse getAllAuthorities() {
        List<AuthorityDTO> authorities = this.authorityService.getAllAuthorities();
        return new GetAllAuthoritiesResponse(authorities);
    }

    // BELOW THIS SHOULD MAYBE BE MOVED TO A DIFFERENT FILE
    @RequestMapping(value = "/level", method = RequestMethod.POST)
    public AuthorityLevelAddedResponse addAuthorityLevel(@Valid @RequestBody AuthorizationLevelRequest request,
                                                    BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.authorityLevelService.authorityLevelExists(request.getAuthorityLevel())) {
            throw new AuthorityLevelAlreadyExists();
        }
        this.authorityLevelService.addAuthorityLevel(request.getAuthorityLevel());  //TODO Move check to service?
        return new AuthorityLevelAddedResponse();
    }

    @RequestMapping(value = "/level", method = RequestMethod.GET)
    public GetAllAuthorityLevelsResponseObject getAllAuthorityLevels() {
        List<AuthorityLevelDTO> authorityLevels = this.authorityLevelService.getAllAuthorityLevels();
        return new GetAllAuthorityLevelsResponse(authorityLevels).toResponseObject();
    }

    @RequestMapping(value = "/level/{id}", method = RequestMethod.DELETE)
    public AuthorityLevelRemovedResponse removeAuthorityLevel(@PathVariable("id") String id) {
        if (this.authorityLevelService.authorityLevelExists(UUID.fromString(id))) {
            throw new AuthorityNotFoundResponse();
        }
        this.authorityLevelService.removeAuthorityLevel(UUID.fromString(id));       // TODO Move check to service?
        return new AuthorityLevelRemovedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public GetAuthorityResponseObject getAuthority(@PathVariable("id") String id) {
        AuthorityDTO authority = this.authorityService.getAuthority(UUID.fromString(id));
        if (authority == null) {
            throw new AuthorityNotFoundResponse();
        }
        return new GetAuthorityResponse(authority).toResponseObject();
    }

}

