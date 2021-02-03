package it.chalmers.gamma.usergdpr.controller;

import it.chalmers.gamma.requests.ChangeGDPRStatusRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.user.service.UserFinder;
import it.chalmers.gamma.usergdpr.response.GDPRStatusEditedResponse;
import it.chalmers.gamma.user.controller.response.GetAllITUsersResponse;
import it.chalmers.gamma.user.controller.response.GetITUserResponse;
import it.chalmers.gamma.user.controller.response.UserNotFoundResponse;
import it.chalmers.gamma.user.service.UserService;
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

    private final UserService userService;
    private final UserFinder userFinder;

    public UserGDPRAdminController(UserService userService, UserFinder userFinder) {
        this.userService = userService;
        this.userFinder = userFinder;
    }

    @PutMapping("/{id}")
    public GDPRStatusEditedResponse editGDPRStatus(@PathVariable("id") String id,
                                                   @Valid @RequestBody ChangeGDPRStatusRequest request,
                                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (!this.userFinder.userExists(UUID.fromString(id))) {
            throw new UserNotFoundResponse();
        }
        this.userService.editGdpr(UUID.fromString(id), request.isGdpr());
        return new GDPRStatusEditedResponse();
    }

    @GetMapping("/minified")
    public GetAllITUsersResponse getAllUserMini() {
        List<GetITUserResponse> userResponses = this.userService.getAllUsers()
                .stream().map(GetITUserResponse::new).collect(Collectors.toList());
        return new GetAllITUsersResponse(userResponses);
    }
}
