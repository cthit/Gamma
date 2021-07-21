package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.AuthorityFacade;
import it.chalmers.gamma.app.domain.AuthorityLevel;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/authority/level")
public final class AuthorityLevelAdminController {

    private final AuthorityFacade authorityFacade;

    public AuthorityLevelAdminController(AuthorityFacade authorityFacade) {
        this.authorityFacade = authorityFacade;
    }

    @PostMapping()
    public AuthorityLevelCreatedResponse addAuthorityLevel(@RequestBody CreateAuthorityLevelRequest request) {
        try {
            this.authorityLevelService.create(request.authorityLevel);
        } catch (AuthorityLevelService.AuthorityLevelAlreadyExistsException e) {
            throw new AuthorityLevelAlreadyExistsResponse();
        }

        return new AuthorityLevelCreatedResponse();
    }

    private record CreateAuthorityLevelRequest(AuthorityLevelName authorityLevel) { }

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
    public AuthorityLevel getAuthoritiesWithLevel(@PathVariable("name") AuthorityLevelName name) {
        try {
            return this.authorityFinder.getByAuthorityLevel(name);
        } catch (AuthorityLevelService.AuthorityLevelNotFoundException e) {
            throw new AuthorityLevelNotFoundResponse();
        }
    }

    private static class AuthorityLevelDeletedResponse extends SuccessResponse { }

    private static class AuthorityLevelCreatedResponse extends SuccessResponse { }

    private static class AuthorityLevelNotFoundResponse extends NotFoundResponse { }

    private static class AuthorityLevelAlreadyExistsResponse extends AlreadyExistsResponse { }

}
