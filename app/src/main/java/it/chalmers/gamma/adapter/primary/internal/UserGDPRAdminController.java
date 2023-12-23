package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.UserGdprTrainingFacade;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

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
    public List<UUID> getUsersWithGdprTraining() {
        return this.userGdprTrainingFacade.getGdprTrained();
    }

    @PutMapping("/{id}")
    public GdprStatusEditedResponse editGDPRStatus(@PathVariable("id") UUID id,
                                                   @RequestBody ChangeGDPRStatusRequest request) {
        this.userGdprTrainingFacade.updateGdprTrainedStatus(id, request.gdpr);
        return new GdprStatusEditedResponse();
    }

    private record ChangeGDPRStatusRequest(boolean gdpr) {
    }

    private static class GdprStatusEditedResponse extends SuccessResponse {
    }

}

