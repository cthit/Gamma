package it.chalmers.gamma.domain.activationcode.controller;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.activationcode.controller.response.ActivationCodeDeletedResponse;
import it.chalmers.gamma.domain.activationcode.controller.response.ActivationCodeNotFoundResponse;
import it.chalmers.gamma.domain.activationcode.controller.response.GetAllActivationCodeResponse;
import it.chalmers.gamma.domain.activationcode.service.ActivationCodeFinder;
import it.chalmers.gamma.domain.activationcode.service.ActivationCodeService;

import it.chalmers.gamma.domain.Cid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/activation_codes")
public final class ActivationCodeAdminController {

    private final ActivationCodeFinder activationCodeFinder;
    private final ActivationCodeService activationCodeService;

    public ActivationCodeAdminController(ActivationCodeFinder activationCodeFinder,
                                         ActivationCodeService activationCodeService) {
        this.activationCodeFinder = activationCodeFinder;
        this.activationCodeService = activationCodeService;
    }

    @GetMapping()
    public GetAllActivationCodeResponse getAllActivationCodes() {
        return new GetAllActivationCodeResponse(this.activationCodeFinder.getAll());
    }

    @DeleteMapping("/{cid}")
    public ActivationCodeDeletedResponse removeActivationCode(@PathVariable("cid") Cid cid) {
        try {
            this.activationCodeService.delete(cid);
            return new ActivationCodeDeletedResponse();
        } catch (EntityNotFoundException e) {
            throw new ActivationCodeNotFoundResponse();
        }
    }
}
