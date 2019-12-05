package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.requests.ChangeGDPRStatusRequest;
import it.chalmers.gamma.response.user.GDPRStatusEditedResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.user.UserNotFoundResponse;
import it.chalmers.gamma.response.user.GetAllITUsersResponse;
import it.chalmers.gamma.response.user.GetAllITUsersResponse.GetAllITUsersResponseObject;
import it.chalmers.gamma.response.user.GetITUserResponse;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/admin/gdpr")
public class GDPRAdminController {

    private final ITUserService itUserService;

    public GDPRAdminController(ITUserService itUserService) {
        this.itUserService = itUserService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
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

    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public GetAllITUsersResponseObject getAllUserMini() {
        List<GetITUserResponse> userResponses = this.itUserService.loadAllUsers()
                .stream().map(GetITUserResponse::new).collect(Collectors.toList());
        return new GetAllITUsersResponse(userResponses).getResponseObject();
    }
}
