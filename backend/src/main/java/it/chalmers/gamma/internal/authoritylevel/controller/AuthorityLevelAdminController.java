package it.chalmers.gamma.internal.authoritylevel.controller;

import it.chalmers.gamma.internal.authority.service.AuthorityDTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.internal.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    private record CreateAuthorityLevelRequest(@Valid AuthorityLevelName authorityLevel) { }

    @GetMapping
    public List<AuthorityLevelName> getAllAuthorityLevels() {
        return this.authorityLevelFinder.getAll();
    }

    @DeleteMapping("/{name}")
    public AuthorityLevelDeletedResponse removeAuthorityLevel(@PathVariable("name") String name) {
        this.authorityLevelService.delete(new AuthorityLevelName(name));
        return new AuthorityLevelDeletedResponse();
    }

    @GetMapping("/{name}")
    public List<AuthorityDTO> getAuthoritiesWithLevel(@PathVariable("name") AuthorityLevelName name) {
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
