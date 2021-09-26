package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.domain.user.UserId;

import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController()
@RequestMapping("/internal/admin/gdpr")
public class UserGDPRAdminController {

    private final UserFacade userFacade;

    public UserGDPRAdminController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    private record ChangeGDPRStatusRequest(boolean gdpr) { }

    @PutMapping("/{id}")
    public GdprStatusEditedResponse editGDPRStatus(@PathVariable("id") UUID id,
                                                   @RequestBody ChangeGDPRStatusRequest request) {
        this.userFacade.updateGdprTrainedStatus(id, request.gdpr);
        return new GdprStatusEditedResponse();
    }

    private static class GdprStatusEditedResponse extends SuccessResponse { }

}

