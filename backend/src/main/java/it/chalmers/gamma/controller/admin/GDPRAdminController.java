package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.requests.ChangeGDPRStatusRequest;
import it.chalmers.gamma.response.GDPRStatusEdited;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.UserNotFoundResponse;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.util.InputValidationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController()
@RequestMapping("/admin/gdpr")
public class GDPRAdminController {

    private final ITUserService itUserService;

    public GDPRAdminController(ITUserService itUserService) {
        this.itUserService = itUserService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> setGDPRStatus(@PathVariable("id") String id,
                                                @Valid @RequestBody ChangeGDPRStatusRequest request,
                                                BindingResult result){
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!itUserService.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        this.itUserService.editGdpr(UUID.fromString(id), request.isGdpr());
        return new GDPRStatusEdited();
    }
}
