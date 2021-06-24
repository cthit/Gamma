package it.chalmers.gamma.app.useractivation.controller;

import it.chalmers.gamma.app.domain.UserActivation;
import it.chalmers.gamma.app.useractivation.service.UserActivationService;

import it.chalmers.gamma.app.domain.Cid;
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

    private final UserActivationService userActivationService;

    public UserActivationAdminController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    @GetMapping()
    public List<UserActivation> getAll() {
        return this.userActivationService.getAll();
    }

    @DeleteMapping("/{cid}")
    public UserActivationDeletedResponse removeUserActivation(@PathVariable("cid") Cid cid) {
        try {
            this.userActivationService.delete(cid);
            return new UserActivationDeletedResponse();
        } catch (UserActivationService.UserActivationNotFoundException e) {
            throw new UserActivationNotFoundResponse();
        }
    }

    private static class UserActivationDeletedResponse extends SuccessResponse { }

    private static class UserActivationNotFoundResponse extends NotFoundResponse { }

}
