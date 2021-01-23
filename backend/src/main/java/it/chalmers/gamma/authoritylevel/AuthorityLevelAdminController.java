package it.chalmers.gamma.authoritylevel;

import it.chalmers.gamma.authority.AuthorityDTO;
import it.chalmers.gamma.authority.AuthorityService;
import it.chalmers.gamma.authority.response.AuthorityDoesNotExistResponse;
import it.chalmers.gamma.authority.response.GetAllAuthoritiesForLevelResponse;
import it.chalmers.gamma.authoritylevel.request.AddAuthorityLevelRequest;
import it.chalmers.gamma.authoritylevel.response.AuthorityLevelAddedResponse;
import it.chalmers.gamma.authoritylevel.response.AuthorityLevelAlreadyExists;
import it.chalmers.gamma.authoritylevel.response.AuthorityLevelRemovedResponse;
import it.chalmers.gamma.authoritylevel.response.GetAllAuthorityLevelsResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.util.InputValidationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/level")
public class AuthorityLevelAdminController {

    private final AuthorityService authorityService;
    private final AuthorityLevelService authorityLevelService;

    public AuthorityLevelAdminController(AuthorityService authorityService, AuthorityLevelService authorityLevelService) {
        this.authorityService = authorityService;
        this.authorityLevelService = authorityLevelService;
    }

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

    @GetMapping
    public GetAllAuthorityLevelsResponse.GetAllAuthorityLevelsResponseObject getAllAuthorityLevels() {
        List<AuthorityLevelDTO> authorityLevels = this.authorityLevelService.getAllAuthorityLevels();
        return new GetAllAuthorityLevelsResponse(authorityLevels).toResponseObject();
    }

    @DeleteMapping("/{id}")
    public AuthorityLevelRemovedResponse removeAuthorityLevel(@PathVariable("id") String id) {
        if (!this.authorityLevelService.authorityLevelExists(id)) {
            throw new AuthorityDoesNotExistResponse();
        }
        AuthorityLevelDTO authorityLevel = this.authorityLevelService.getAuthorityLevelDTO(id);
        this.authorityService.removeAllAuthoritiesWithAuthorityLevel(authorityLevel);
        this.authorityLevelService.removeAuthorityLevel(UUID.fromString(id));       // TODO Move check to service?
        return new AuthorityLevelRemovedResponse();
    }

    @GetMapping("/{id}")
    public GetAllAuthoritiesForLevelResponse.GetAllAuthoritiesForLevelResponseObject getAuthoritiesWithLevel(
            @PathVariable("id") String id) {
        List<AuthorityDTO> authorities = this.authorityService.getAuthoritiesWithLevel(UUID.fromString(id));
        AuthorityLevelDTO authorityLevel = this.authorityLevelService.getAuthorityLevelDTO(id);
        return new GetAllAuthoritiesForLevelResponse(authorities, authorityLevel.getAuthority()).toResponseObject();
    }

}
