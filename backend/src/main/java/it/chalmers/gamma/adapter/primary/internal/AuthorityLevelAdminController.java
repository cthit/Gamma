package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/authority/level")
public final class AuthorityLevelAdminController {

    private final AuthorityLevelFacade authorityLevelFacade;

    public AuthorityLevelAdminController(AuthorityLevelFacade authorityLevelFacade) {
        this.authorityLevelFacade = authorityLevelFacade;
    }

    @PostMapping()
    public AuthorityLevelCreatedResponse addAuthorityLevel(@RequestBody CreateAuthorityLevelRequest request) {
        try {
            this.authorityLevelFacade.create(request.authorityLevel);
        } catch (AuthorityLevelRepository.AuthorityLevelAlreadyExistsException e) {
            throw new AuthorityLevelAlreadyExistsResponse();
        }
        return new AuthorityLevelCreatedResponse();
    }

    @GetMapping
    public List<AuthorityLevelFacade.AuthorityLevelDTO> getAllAuthorityLevels() {
        return this.authorityLevelFacade.getAll();
    }

    @DeleteMapping("/{name}")
    public AuthorityLevelDeletedResponse deleteAuthorityLevel(@PathVariable("name") String name) {
        try {
            this.authorityLevelFacade.delete(name);
        } catch (AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
            throw new AuthorityLevelAlreadyExistsResponse();
        }
        return new AuthorityLevelDeletedResponse();
    }

    @GetMapping("/{name}")
    public AuthorityLevelFacade.AuthorityLevelDTO getAuthorityLevel(@PathVariable("name") String name) {
        return this.authorityLevelFacade.get(name)
                .orElseThrow(AuthorityLevelNotFoundResponse::new);
    }

    private record CreateAuthorityLevelRequest(String authorityLevel) {
    }

    private static class AuthorityLevelDeletedResponse extends SuccessResponse {
    }

    private static class AuthorityLevelCreatedResponse extends SuccessResponse {
    }

    private static class AuthorityLevelNotFoundResponse extends NotFoundResponse {
    }

    private static class AuthorityLevelAlreadyExistsResponse extends AlreadyExistsResponse {
    }

}
