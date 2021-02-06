package it.chalmers.gamma.usergdpr.controller;

import it.chalmers.gamma.requests.ChangeGDPRStatusRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.controller.response.UserNotFoundResponse;
import it.chalmers.gamma.user.exception.UserNotFoundException;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.usergdpr.response.GDPRStatusEditedResponse;
import it.chalmers.gamma.user.controller.response.GetAllITUsersResponse;
import it.chalmers.gamma.user.controller.response.GetITUserResponse;
import it.chalmers.gamma.usergdpr.response.UsersWithGDPRResponse;
import it.chalmers.gamma.usergdpr.service.UserGDPRService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;
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
