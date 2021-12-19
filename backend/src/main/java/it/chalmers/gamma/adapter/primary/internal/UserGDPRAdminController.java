package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.UserGdprTrainingFacade;

import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/internal/admin/gdpr")
public class UserGDPRAdminController {

    private final UserGdprTrainingFacade userGdprTrainingFacade;

    public UserGDPRAdminController(UserGdprTrainingFacade userGdprTrainingFacade) {
        this.userGdprTrainingFacade = userGdprTrainingFacade;
    }

    @GetMapping()
    public List<UserGdprTrainingFacade.UserGdprTrainedDTO> getUsersWithGdprTraining() {
        return this.userGdprTrainingFacade.getUsersWithGdprTrained();
    }

    private record ChangeGDPRStatusRequest(boolean gdpr) {
    }

    @PutMapping("/{id}")
    public GdprStatusEditedResponse editGDPRStatus(@PathVariable("id") UUID id,
                                                   @RequestBody ChangeGDPRStatusRequest request) {
        this.userGdprTrainingFacade.updateGdprTrainedStatus(id, request.gdpr);
        return new GdprStatusEditedResponse();
    }

    private static class GdprStatusEditedResponse extends SuccessResponse { }

}

