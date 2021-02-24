package it.chalmers.gamma.domain.authority.controller;

import it.chalmers.gamma.domain.EntityAlreadyExistsException;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.authority.controller.response.*;
import it.chalmers.gamma.domain.authority.data.db.AuthorityPK;
import it.chalmers.gamma.domain.authority.data.dto.AuthorityShallowDTO;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.authority.service.AuthorityService;
import it.chalmers.gamma.domain.authority.controller.request.CreateAuthorityRequest;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

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

    public AuthorityAdminController(AuthorityFinder authorityFinder, AuthorityService authorityService) {
        this.authorityFinder = authorityFinder;
        this.authorityService = authorityService;
    }

    @PostMapping
    public AuthorityCreatedResponse addAuthority(@RequestBody CreateAuthorityRequest request) {
        try {
            this.authorityService.create(
                    new AuthorityShallowDTO(
                        request.superGroupId,
                        request.postId,
                        new AuthorityLevelName(request.authority)
                    )
            );
            return new AuthorityCreatedResponse();
        } catch (EntityAlreadyExistsException e) {
            throw new AuthorityAlreadyExistsResponse();
        }
    }

    @DeleteMapping("/{superGroupId}/{postId}/{authorityLevelName}")
    public AuthorityRemovedResponse removeAuthority(@PathVariable("superGroupId") SuperGroupId superGroupId,
                                                    @PathVariable("postId") PostId postId,
                                                    @PathVariable("authorityLevelName") String authorityLevelName) {
        try {
            this.authorityService.delete(
                    new AuthorityPK(superGroupId, postId, new AuthorityLevelName(authorityLevelName))
            );
            return new AuthorityRemovedResponse();
        } catch (EntityNotFoundException e) {
            throw new AuthorityNotFoundResponse();
        }
    }

    @GetMapping()
    public GetAllAuthoritiesResponse getAllAuthorities() {
        return new GetAllAuthoritiesResponse(this.authorityFinder.getAll());
    }

}

