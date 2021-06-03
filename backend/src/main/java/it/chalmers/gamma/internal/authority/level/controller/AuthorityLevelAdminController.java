package it.chalmers.gamma.internal.authority.level.controller;

import it.chalmers.gamma.domain.Authorities;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostDTO;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelService;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostService;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/internal/admin/authority/level")
public class AuthorityLevelAdminController {

    private final AuthorityFinder authorityFinder;
    private final AuthorityLevelService authorityLevelService;

    public AuthorityLevelAdminController(AuthorityFinder authorityFinder,
                                         AuthorityLevelService authorityLevelService) {
        this.authorityFinder = authorityFinder;
        this.authorityLevelService = authorityLevelService;
    }

    @PostMapping()
    public AuthorityLevelCreatedResponse addAuthorityLevel(@Valid @RequestBody CreateAuthorityLevelRequest request) {
        try {
            this.authorityLevelService.create(request.authorityLevel);
        } catch (AuthorityLevelService.AuthorityLevelAlreadyExistsException e) {
            throw new AuthorityLevelAlreadyExistsResponse();
        }

        return new AuthorityLevelCreatedResponse();
    }

    private record CreateAuthorityLevelRequest(@Valid AuthorityLevelName authorityLevel) { }

    @GetMapping
    public List<AuthorityLevelName> getAllAuthorityLevels() {
        return this.authorityLevelService.getAll();
    }

    @DeleteMapping("/{name}")
    public AuthorityLevelDeletedResponse removeAuthorityLevel(@PathVariable("name") AuthorityLevelName name) {
        try {
            this.authorityLevelService.delete(name);
            return new AuthorityLevelDeletedResponse();
        } catch (AuthorityLevelService.AuthorityLevelNotFoundException e) {
            throw new AuthorityLevelNotFoundResponse();
        }
    }

    @GetMapping("/{name}")
    public Authorities getAuthoritiesWithLevel(@PathVariable("name") AuthorityLevelName name) {
        try {
            return this.authorityFinder.getByAuthorityLevel(name);
        } catch (EntityNotFoundException e) {
            throw new AuthorityLevelNotFoundResponse();
        }
    }

    private static class AuthorityLevelDeletedResponse extends SuccessResponse { }

    private static class AuthorityLevelCreatedResponse extends SuccessResponse { }

    private static class AuthorityLevelNotFoundResponse extends ErrorResponse {
        private AuthorityLevelNotFoundResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static class AuthorityLevelAlreadyExistsResponse extends ErrorResponse {
        private AuthorityLevelAlreadyExistsResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
