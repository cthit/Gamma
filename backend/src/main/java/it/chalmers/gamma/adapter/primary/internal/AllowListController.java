package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/internal/allow-list")
public final class AllowListController {

    private final UserCreationFacade userCreationFacade;

    public AllowListController(UserCreationFacade userCreationFacade) {
        this.userCreationFacade = userCreationFacade;
    }

    @PostMapping("/activate-cid")
    public AllowListedCidActivatedResponse createActivationCode(@RequestBody AllowCodeRequest request) {
        try {
            this.userCreationFacade.tryToActivateUser(request.cid);
        } finally {
            //Gamma doesn't differentiate if activation of a cid was successful or not.
            return new AllowListedCidActivatedResponse();
        }
    }

    private record AllowCodeRequest(String cid) {
    }

    // This will be thrown even if there was an error for security reasons.
    private static class AllowListedCidActivatedResponse extends SuccessResponse {
    }

}

