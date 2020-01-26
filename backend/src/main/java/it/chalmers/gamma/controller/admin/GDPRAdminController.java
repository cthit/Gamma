package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.requests.ChangeGDPRStatusRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.user.GDPRStatusEditedResponse;
import it.chalmers.gamma.response.user.GetAllITUsersMinifiedResponse;
import it.chalmers.gamma.response.user.GetAllITUsersMinifiedResponse.GetAllITUsersMinifiedResponseObject;
import it.chalmers.gamma.response.user.GetITUserMinifiedResponse;
import it.chalmers.gamma.response.user.UserNotFoundResponse;
import it.chalmers.gamma.service.ITUserService;
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
public class GDPRAdminController {

    private final ITUserService itUserService;

    public GDPRAdminController(ITUserService itUserService) {
        this.itUserService = itUserService;
    }

    @PutMapping("/{id}")
    public GDPRStatusEditedResponse editGDPRStatus(@PathVariable("id") String id,
                                                   @Valid @RequestBody ChangeGDPRStatusRequest request,
                                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.itUserService.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        this.itUserService.editGdpr(UUID.fromString(id), request.isGdpr());
        return new GDPRStatusEditedResponse();
    }

    @GetMapping("/minified")
    public GetAllITUsersMinifiedResponseObject getAllUserMini() {
        List<GetITUserMinifiedResponse> userResponses = this.itUserService.loadAllUsers()
                .stream().map(GetITUserMinifiedResponse::new).collect(Collectors.toList());
        return new GetAllITUsersMinifiedResponse(userResponses).toResponseObject();
    }
}
