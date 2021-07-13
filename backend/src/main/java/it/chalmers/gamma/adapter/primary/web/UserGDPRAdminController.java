package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.domain.UserGDPRTraining;
import it.chalmers.gamma.app.user.UserGDPRTrainingService;

import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/internal/admin/gdpr")
public class UserGDPRAdminController {

    private final UserGDPRTrainingService userGDPRTrainingService;

    public UserGDPRAdminController(UserGDPRTrainingService userGDPRTrainingService) {
        this.userGDPRTrainingService = userGDPRTrainingService;
    }

    private record ChangeGDPRStatusRequest(boolean gdpr) { }

    @PutMapping("/{id}")
    public GdprStatusEditedResponse editGDPRStatus(@PathVariable("id") UserId id,
                                                   @RequestBody ChangeGDPRStatusRequest request) {
        userGDPRTrainingService.editGDPR(id, request.gdpr);
        return new GdprStatusEditedResponse();
    }

    @GetMapping()
    public List<UserGDPRTraining> getAllUser() {
        return userGDPRTrainingService.getUsersWithGDPR();
    }

    private static class GdprStatusEditedResponse extends SuccessResponse { }

}

