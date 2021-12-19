package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.user.activation.ActivationCodeFacade;

import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/admin/user-activation")
public final class UserActivationAdminController {

    private final ActivationCodeFacade activationCodeFacade;

    public UserActivationAdminController(ActivationCodeFacade activationCodeFacade) {
        this.activationCodeFacade = activationCodeFacade;
    }

    @GetMapping()
    public List<ActivationCodeFacade.UserActivationDTO> getAll() {
        return this.activationCodeFacade.getAllUserActivations();
    }

    @GetMapping("/{cid}")
    public ActivationCodeFacade.UserActivationDTO get(@PathVariable("cid") String cid) {
        return this.activationCodeFacade.get(cid)
                .orElseThrow(UserActivationNotFoundResponse::new);
    }

    @DeleteMapping("/{cid}")
    public UserActivationDeletedResponse removeUserActivation(@PathVariable("cid") String cid) {
        this.activationCodeFacade.removeUserActivation(cid);
        return new UserActivationDeletedResponse();
    }

    private static class UserActivationDeletedResponse extends SuccessResponse { }

    private static class UserActivationNotFoundResponse extends NotFoundResponse { }

}
