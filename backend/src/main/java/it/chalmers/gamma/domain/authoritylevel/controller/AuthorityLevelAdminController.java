package it.chalmers.gamma.domain.authoritylevel.controller;

import it.chalmers.gamma.domain.authority.data.AuthorityDTO;
import it.chalmers.gamma.domain.authority.service.AuthorityFinder;
import it.chalmers.gamma.domain.authority.controller.response.AuthorityDoesNotExistResponse;
import it.chalmers.gamma.domain.authority.controller.response.GetAllAuthoritiesForLevelResponse;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.controller.response.*;
import it.chalmers.gamma.domain.authoritylevel.exception.AuthorityLevelAlreadyExistsException;
import it.chalmers.gamma.domain.authoritylevel.exception.AuthorityLevelNotFoundException;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelFinder;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.domain.authoritylevel.controller.request.AddAuthorityLevelRequest;
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
        return new GetAllAuthorityLevelsResponse(this.authorityLevelFinder.getAuthorityLevels()).toResponseObject();
    }

    @DeleteMapping("/{name}")
    public AuthorityLevelRemovedResponse removeAuthorityLevel(@PathVariable("name") String name) {
        try {
            this.authorityLevelService.removeAuthorityLevel(new AuthorityLevelName(name));
        } catch (AuthorityLevelNotFoundException e) {
            LOGGER.error("AUTHORITY_LEVEL_NOT_FOUND", e);
            throw new AuthorityDoesNotExistResponse();
        }

        return new AuthorityLevelRemovedResponse();
    }

    @GetMapping("/{name}")
    public GetAllAuthoritiesForLevelResponse.GetAllAuthoritiesForLevelResponseObject getAuthoritiesWithLevel(
            @PathVariable("name") String name) {
        try {
            return new GetAllAuthoritiesForLevelResponse(
                    this.authorityFinder.getAuthoritiesWithAuthorityLevel(new AuthorityLevelName(name))
            ).toResponseObject();
        } catch (AuthorityLevelNotFoundException e) {
            LOGGER.error("AUTHORITY_LEVEL_NOT_FOUND", e);
            throw new AuthorityLevelDoesNotExistResponse();
        }
    }

}
