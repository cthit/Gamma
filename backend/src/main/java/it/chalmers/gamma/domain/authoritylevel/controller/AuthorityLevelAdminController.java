package it.chalmers.gamma.domain.authoritylevel.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/level")
public class AuthorityLevelAdminController {

    private final AuthorityFinder authorityFinder;
    private final AuthorityLevelService authorityLevelService;
    private final AuthorityLevelFinder authorityLevelFinder;

    public AuthorityLevelAdminController(AuthorityFinder authorityFinder,
                                         AuthorityLevelService authorityLevelService,
                                         AuthorityLevelFinder authorityLevelFinder) {
        this.authorityFinder = authorityFinder;
        this.authorityLevelService = authorityLevelService;
        this.authorityLevelFinder = authorityLevelFinder;
    }

    @PostMapping("/level")
    public AuthorityLevelCreatedResponse addAuthorityLevel(@Valid @RequestBody CreateAuthorityLevelRequest request) {
        try {
            this.authorityLevelService.create(request.authorityLevel);
        } catch (EntityAlreadyExistsException e) {
            throw new AuthorityLevelAlreadyExistsResponse();
        }

        return new AuthorityLevelCreatedResponse();
    }

    @GetMapping
    public GetAllAuthorityLevelsResponse getAllAuthorityLevels() {
        return new GetAllAuthorityLevelsResponse(this.authorityLevelFinder.getAll());
    }

    @DeleteMapping("/{name}")
    public AuthorityLevelDeletedResponse removeAuthorityLevel(@PathVariable("name") String name) {
        this.authorityLevelService.delete(new AuthorityLevelName(name));
        return new AuthorityLevelDeletedResponse();
    }

    @GetMapping("/{name}")
    public GetAllAuthoritiesForLevelResponse getAuthoritiesWithLevel(@PathVariable("name") AuthorityLevelName name) {
        try {
            return new GetAllAuthoritiesForLevelResponse(
                    this.authorityFinder.getByAuthorityLevel(name)
            );
        } catch (EntityNotFoundException e) {
            throw new AuthorityLevelNotFoundResponse();
        }
    }

}
