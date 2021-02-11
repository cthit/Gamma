package it.chalmers.gamma.domain.usergdpr.controller;

import it.chalmers.gamma.requests.ChangeGDPRStatusRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.user.controller.response.UserNotFoundResponse;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.usergdpr.controller.response.GDPRStatusEditedResponse;
import it.chalmers.gamma.domain.usergdpr.controller.response.UsersWithGDPRResponse;
import it.chalmers.gamma.domain.usergdpr.service.UserGDPRService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.UUID;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/admin/gdpr")
public class UserGDPRAdminController {

    private final UserGDPRService userGDPRService;
    private final UserFinder userFinder;

    public UserGDPRAdminController(UserGDPRService userGDPRService, UserFinder userFinder) {
        this.userGDPRService = userGDPRService;
        this.userFinder = userFinder;
    }

    @PutMapping("/{id}")
    public GDPRStatusEditedResponse editGDPRStatus(@PathVariable("id") UUID id,
                                                   @Valid @RequestBody ChangeGDPRStatusRequest request,
                                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        try {
            userGDPRService.editGDPR(id, request.isGdpr());
        } catch (UserNotFoundException e) {
            throw new UserNotFoundResponse();
        }

        return new GDPRStatusEditedResponse();
    }

    @GetMapping("/minified")
    public UsersWithGDPRResponse.UsersWithGDPRResponseObject getAllUserMini() {
        return new UsersWithGDPRResponse(userGDPRService.getUsersWithGDPR()).toResponseObject();
    }
}
