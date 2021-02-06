package it.chalmers.gamma.authoritylevel.controller;

import it.chalmers.gamma.authority.AuthorityDTO;
import it.chalmers.gamma.authority.AuthorityFinder;
import it.chalmers.gamma.authority.response.AuthorityDoesNotExistResponse;
import it.chalmers.gamma.authority.response.GetAllAuthoritiesForLevelResponse;
import it.chalmers.gamma.authoritylevel.controller.response.*;
import it.chalmers.gamma.authoritylevel.dto.AuthorityLevelDTO;
import it.chalmers.gamma.authoritylevel.exception.AuthorityLevelAlreadyExistsException;
import it.chalmers.gamma.authoritylevel.exception.AuthorityLevelNotFoundException;
import it.chalmers.gamma.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.authoritylevel.controller.request.AddAuthorityLevelRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.util.InputValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/level")
public class AuthorityLevelAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityLevelAdminController.class);

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
    public AuthorityLevelAddedResponse addAuthorityLevel(@Valid @RequestBody AddAuthorityLevelRequest request,
                                                         BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        try {
            this.authorityLevelService.addAuthorityLevel(request.getAuthorityLevel());
        } catch (AuthorityLevelAlreadyExistsException e) {
            throw new AuthorityLevelAlreadyExistsResponse();
        }
        return new AuthorityLevelAddedResponse();
    }

    @GetMapping
    public GetAllAuthorityLevelsResponse.GetAllAuthorityLevelsResponseObject getAllAuthorityLevels() {
        List<AuthorityLevelDTO> authorityLevels = this.authorityLevelFinder.getAllAuthorityLevels();
        return new GetAllAuthorityLevelsResponse(authorityLevels).toResponseObject();
    }

    @DeleteMapping("/{id}")
    public AuthorityLevelRemovedResponse removeAuthorityLevel(@PathVariable("id") UUID id) {
        AuthorityLevelDTO authorityLevel = null;
        try {
            this.authorityLevelService.removeAuthorityLevel(id);
        } catch (AuthorityLevelNotFoundException e) {
            LOGGER.error("AUTHORITY_LEVEL_NOT_FOUND", e);
            throw new AuthorityDoesNotExistResponse();
        }

        return new AuthorityLevelRemovedResponse();
    }

    @GetMapping("/{id}")
    public GetAllAuthoritiesForLevelResponse.GetAllAuthoritiesForLevelResponseObject getAuthoritiesWithLevel(
            @PathVariable("id") UUID id) {
        try {
            List<AuthorityDTO> authorities = this.authorityFinder.getAuthoritiesWithAuthorityLevel(id);
            AuthorityLevelDTO authorityLevel = this.authorityLevelFinder.getAuthorityLevel(id);

            return new GetAllAuthoritiesForLevelResponse(authorities, authorityLevel.getAuthority()).toResponseObject();
        } catch (AuthorityLevelNotFoundException e) {
            LOGGER.error("AUTHORITY_LEVEL_NOT_FOUND", e);
            throw new AuthorityLevelDoesNotExistResponse();
        }
    }

}
