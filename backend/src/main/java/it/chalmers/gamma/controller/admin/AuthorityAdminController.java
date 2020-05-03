package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.requests.AddAuthorityLevelRequest;
import it.chalmers.gamma.requests.AddAuthorityRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.authority.AuthorityAddedResponse;
import it.chalmers.gamma.response.authority.AuthorityDoesNotExistResponse;
import it.chalmers.gamma.response.authority.AuthorityLevelAddedResponse;
import it.chalmers.gamma.response.authority.AuthorityLevelAlreadyExists;
import it.chalmers.gamma.response.authority.AuthorityLevelRemovedResponse;
import it.chalmers.gamma.response.authority.AuthorityRemovedResponse;
import it.chalmers.gamma.response.authority.GetAllAuthoritiesForLevelResponse;
import it.chalmers.gamma.response.authority.GetAllAuthoritiesResponse;
import it.chalmers.gamma.response.authority.GetAllAuthorityLevelsResponse;
import it.chalmers.gamma.response.authority.GetAllAuthorityLevelsResponse.GetAllAuthorityLevelsResponseObject;
import it.chalmers.gamma.response.authority.GetAuthorityResponse;
import it.chalmers.gamma.response.authority.GetAuthorityResponse.GetAuthorityResponseObject;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.PostService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping()
    public AuthorityAddedResponse addAuthority(@Valid @RequestBody AddAuthorityRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        PostDTO post = this.postService.getPostDTO(request.getPost());
        FKITSuperGroupDTO group = this.fkitSuperGroupService.getGroupDTO(request.getSuperGroup());
        AuthorityLevelDTO level = this.authorityLevelService.getAuthorityLevelDTO(request.getAuthority());
        this.authorityService.createAuthority(group, post, level);
        return new AuthorityAddedResponse();
    }

    @DeleteMapping("/{id}")
    public AuthorityRemovedResponse removeAuthority(@PathVariable("id") String id) {
        if (!this.authorityService.authorityExists(id)) {
            throw new AuthorityDoesNotExistResponse();
        }
        this.authorityService.removeAuthority(UUID.fromString(id)); // TODO move check to service?
        return new AuthorityRemovedResponse();
    }

    @GetMapping()
    public GetAllAuthoritiesResponse getAllAuthorities() {
        List<AuthorityDTO> authorities = this.authorityService.getAllAuthorities();
        return new GetAllAuthoritiesResponse(authorities);
    }

    // BELOW THIS SHOULD MAYBE BE MOVED TO A DIFFERENT FILE
    @PostMapping("/level")
    public AuthorityLevelAddedResponse addAuthorityLevel(@Valid @RequestBody AddAuthorityLevelRequest request,
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

    @GetMapping("/level")
    public GetAllAuthorityLevelsResponseObject getAllAuthorityLevels() {
        List<AuthorityLevelDTO> authorityLevels = this.authorityLevelService.getAllAuthorityLevels();
        return new GetAllAuthorityLevelsResponse(authorityLevels).toResponseObject();
    }

    @DeleteMapping("/level/{id}")
    public AuthorityLevelRemovedResponse removeAuthorityLevel(@PathVariable("id") String id) {
        if (!this.authorityLevelService.authorityLevelExists(id)) {
            throw new AuthorityDoesNotExistResponse();
        }
        AuthorityLevelDTO authorityLevel = this.authorityLevelService.getAuthorityLevelDTO(id);
        this.authorityService.removeAllAuthoritiesWithAuthorityLevel(authorityLevel);
        this.authorityLevelService.removeAuthorityLevel(UUID.fromString(id));       // TODO Move check to service?
        return new AuthorityLevelRemovedResponse();
    }

    @GetMapping("/level/{id}")
    public GetAllAuthoritiesForLevelResponse.GetAllAuthoritiesForLevelResponseObject getAuthoritiesWithLevel(
            @PathVariable("id") String id) {
        List<AuthorityDTO> authorities = this.authorityService.getAuthoritiesWithLevel(UUID.fromString(id));
        AuthorityLevelDTO authorityLevel = this.authorityLevelService.getAuthorityLevelDTO(id);
        return new GetAllAuthoritiesForLevelResponse(authorities, authorityLevel.getAuthority()).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetAuthorityResponseObject getAuthority(@PathVariable("id") String id) {
        AuthorityDTO authority = this.authorityService.getAuthority(UUID.fromString(id));
        return new GetAuthorityResponse(authority).toResponseObject();
    }

}

