package it.chalmers.gamma.domain.usergdpr.controller;

import it.chalmers.gamma.domain.user.service.UserId;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.usergdpr.service.UserGDPRTrainingService;

import javax.validation.Valid;

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
                                                   @Valid @RequestBody ChangeGDPRStatusRequest request) {
        userGDPRTrainingService.editGDPR(id, request.isGdpr());

        return new GDPRStatusEditedResponse();
    }

    @GetMapping("/minified")
    public UsersWithGDPRResponse.UsersWithGDPRResponseObject getAllUserMini() {
        return new UsersWithGDPRResponse(userGDPRTrainingService.getUsersWithGDPR()).toResponseObject();
    }
}
