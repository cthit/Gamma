package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.UserCreationFacade;
import it.chalmers.gamma.domain.user.Cid;

import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/internal/whitelist")
public final class WhitelistController {

    private final UserCreationFacade userCreationFacade;

    public WhitelistController(UserCreationFacade userCreationFacade) {
        this.userCreationFacade = userCreationFacade;
    }

    private record WhitelistCodeRequest(Cid cid) { }

    @PostMapping("/activate_cid")
    public WhitelistedCidActivatedResponse createActivationCode(@RequestBody WhitelistCodeRequest request) {
        this.userCreationFacade.tryToActivateUser(request.cid);

        //Gamma doesn't differentiate if activation of a cid was successful or not.
        return new WhitelistedCidActivatedResponse();
    }

    // This will be thrown even if there was an error for security reasons.
    private static class WhitelistedCidActivatedResponse extends SuccessResponse { }

}

