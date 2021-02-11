package it.chalmers.gamma.domain.authority.controller;

import it.chalmers.gamma.domain.authority.controller.response.*;
import it.chalmers.gamma.domain.authority.data.AuthorityDTO;
import it.chalmers.gamma.domain.authority.exception.AuthorityAlreadyExistsException;
import it.chalmers.gamma.domain.authority.exception.AuthorityNotFoundException;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.authority.service.AuthorityService;
import it.chalmers.gamma.domain.authority.controller.request.AddAuthorityRequest;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupService;
import it.chalmers.gamma.domain.post.service.PostService;
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

    private final AuthorityFinder authorityFinder;
    private final AuthorityService authorityService;
    private final PostService postService;
    private final AuthorityLevelService authorityLevelService;
    private final SuperGroupService superGroupService;

    public AuthorityAdminController(AuthorityFinder authorityFinder, AuthorityService authorityService,
                                    PostService postService,
                                    AuthorityLevelService authorityLevelService,
                                    SuperGroupService superGroupService) {
        this.authorityFinder = authorityFinder;
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
        try {
            this.authorityService.createAuthority(
                    request.getSuperGroupId(),
                    request.getPostId(),
                    new AuthorityLevelName(request.getAuthority())
            );
            return new AuthorityAddedResponse();
        } catch (AuthorityAlreadyExistsException e) {
            throw new AuthorityAlreadyExistsResponse();
        }
    }

    @DeleteMapping("/{superGroupId}/{postId}/{authorityLevelName}")
    public AuthorityRemovedResponse removeAuthority(@PathVariable("superGroupId") UUID superGroupId,
                                                    @PathVariable("postId") UUID postId,
                                                    @PathVariable("authorityLevelName") String authorityLevelName) {
        try {
            this.authorityService.removeAuthority(superGroupId, postId, new AuthorityLevelName(authorityLevelName));
            return new AuthorityRemovedResponse();
        } catch (AuthorityNotFoundException e) {
            throw new AuthorityDoesNotExistResponse();
        }
    }

    @GetMapping()
    public GetAllAuthoritiesResponse getAllAuthorities() {
        return new GetAllAuthoritiesResponse(this.authorityFinder.getAuthorities());
    }

}

