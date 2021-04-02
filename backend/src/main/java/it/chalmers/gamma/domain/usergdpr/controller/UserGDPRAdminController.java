package it.chalmers.gamma.domain.usergdpr.controller;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.usergdpr.controller.request.ChangeGDPRStatusRequest;
import it.chalmers.gamma.util.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.usergdpr.controller.response.GDPRStatusEditedResponse;
import it.chalmers.gamma.domain.usergdpr.controller.response.UsersWithGDPRResponse;
import it.chalmers.gamma.domain.usergdpr.service.UserGDPRTrainingService;
import it.chalmers.gamma.util.InputValidationUtils;

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

    private final UserGDPRTrainingService userGDPRTrainingService;

    public UserGDPRAdminController(UserGDPRTrainingService userGDPRTrainingService, UserFinder userFinder) {
        this.userGDPRTrainingService = userGDPRTrainingService;
    }

    @PutMapping("/{id}")
    public GDPRStatusEditedResponse editGDPRStatus(@PathVariable("id") UserId id,
                                                   @Valid @RequestBody ChangeGDPRStatusRequest request,
                                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        userGDPRTrainingService.editGDPR(id, request.isGdpr());

        return new GDPRStatusEditedResponse();
    }

    @GetMapping("/minified")
    public UsersWithGDPRResponse.UsersWithGDPRResponseObject getAllUserMini() {
        return new UsersWithGDPRResponse(userGDPRTrainingService.getUsersWithGDPR()).toResponseObject();
    }
}
