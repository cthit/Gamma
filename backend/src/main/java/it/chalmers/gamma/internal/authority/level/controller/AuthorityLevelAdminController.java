package it.chalmers.gamma.internal.authority.level.controller;

import it.chalmers.gamma.internal.authority.post.service.AuthorityPostDTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.internal.authority.post.service.AuthorityPostFinder;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelFinder;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/authority/level")
public class AuthorityLevelAdminController {

    private final AuthorityPostFinder authorityPostFinder;
    private final AuthorityLevelService authorityLevelService;
    private final AuthorityLevelFinder authorityLevelFinder;

    public AuthorityLevelAdminController(AuthorityPostFinder authorityPostFinder,
                                         AuthorityLevelService authorityLevelService,
                                         AuthorityLevelFinder authorityLevelFinder) {
        this.authorityPostFinder = authorityPostFinder;
        this.authorityLevelService = authorityLevelService;
        this.authorityLevelFinder = authorityLevelFinder;
    }

    @PostMapping()
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
    public AuthorityLevelDeletedResponse removeAuthorityLevel(@PathVariable("name") AuthorityLevelName name) {
        this.authorityLevelService.delete(name);
        return new AuthorityLevelDeletedResponse();
    }

    @GetMapping("/{name}")
    public List<AuthorityPostDTO> getAuthoritiesWithLevel(@PathVariable("name") AuthorityLevelName name) {
        try {
            return this.authorityPostFinder.getByAuthorityLevel(name);
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
