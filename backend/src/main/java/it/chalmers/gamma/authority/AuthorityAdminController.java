package it.chalmers.gamma.authority;

import it.chalmers.gamma.authoritylevel.request.AddAuthorityLevelRequest;
import it.chalmers.gamma.authority.request.AddAuthorityRequest;
import it.chalmers.gamma.authoritylevel.AuthorityLevelDTO;
import it.chalmers.gamma.authoritylevel.AuthorityLevelService;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.authority.response.AuthorityAddedResponse;
import it.chalmers.gamma.authority.response.AuthorityDoesNotExistResponse;
import it.chalmers.gamma.authoritylevel.response.AuthorityLevelAddedResponse;
import it.chalmers.gamma.authoritylevel.response.AuthorityLevelAlreadyExists;
import it.chalmers.gamma.authoritylevel.response.AuthorityLevelRemovedResponse;
import it.chalmers.gamma.authority.response.AuthorityRemovedResponse;
import it.chalmers.gamma.authority.response.GetAllAuthoritiesForLevelResponse;
import it.chalmers.gamma.authority.response.GetAllAuthoritiesResponse;
import it.chalmers.gamma.authoritylevel.response.GetAllAuthorityLevelsResponse;
import it.chalmers.gamma.authoritylevel.response.GetAllAuthorityLevelsResponse.GetAllAuthorityLevelsResponseObject;
import it.chalmers.gamma.authority.response.GetAuthorityResponse;
import it.chalmers.gamma.authority.response.GetAuthorityResponse.GetAuthorityResponseObject;
import it.chalmers.gamma.supergroup.SuperGroupService;
import it.chalmers.gamma.post.PostService;
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

@RestController
@RequestMapping("/admin/authority")
public final class AuthorityAdminController {

    private final AuthorityService authorityService;
    private final PostService postService;
    private final AuthorityLevelService authorityLevelService;
    private final SuperGroupService superGroupService;

    public AuthorityAdminController(AuthorityService authorityService,
                                    PostService postService,
                                    AuthorityLevelService authorityLevelService,
                                    SuperGroupService superGroupService) {
        this.authorityService = authorityService;
        this.postService = postService;
        this.authorityLevelService = authorityLevelService;
        this.superGroupService = superGroupService;
    }

    @PostMapping
    public AuthorityAddedResponse addAuthority(@Valid @RequestBody AddAuthorityRequest request, BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        this.authorityService.createAuthority(request.getSuperGroup(), request.getPost(), request.getAuthority());
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


    @GetMapping("/{id}")
    public GetAuthorityResponseObject getAuthority(@PathVariable("id") String id) {
        AuthorityDTO authority = this.authorityService.getAuthority(UUID.fromString(id));
        return new GetAuthorityResponse(authority).toResponseObject();
    }

}

