package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.activationcode.ActivationCodeFinder;
import it.chalmers.gamma.activationcode.exception.ActivationCodeNotFoundException;
import it.chalmers.gamma.activationcode.response.ActivationCodeDeletedResponse;
import it.chalmers.gamma.activationcode.response.ActivationCodeDoesNotExistResponse;
import it.chalmers.gamma.activationcode.response.GetActivationCodeResponse;
import it.chalmers.gamma.activationcode.response.GetActivationCodeResponse.GetActivationCodeResponseObject;
import it.chalmers.gamma.activationcode.response.GetAllActivationCodesResponse;
import it.chalmers.gamma.activationcode.response.GetAllActivationCodesResponse.GetAllActivationCodesResponseObject;
import it.chalmers.gamma.activationcode.ActivationCodeService;

import it.chalmers.gamma.domain.Cid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/activation_codes")
public final class ActivationCodeAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivationCodeAdminController.class);

    private final ActivationCodeFinder activationCodeFinder;
    private final ActivationCodeService activationCodeService;

    public ActivationCodeAdminController(ActivationCodeFinder activationCodeFinder,
                                         ActivationCodeService activationCodeService) {
        this.activationCodeFinder = activationCodeFinder;
        this.activationCodeService = activationCodeService;
    }

    @GetMapping()
    public GetAllActivationCodesResponseObject getAllActivationCodes() {
        return new GetAllActivationCodesResponse(
                this.activationCodeFinder.getActivationCodes()
        ).toResponseObject();
    }

    @GetMapping("/{cid}")
    public GetActivationCodeResponseObject getActivationCode(
            @PathVariable("cid") String cid) {
        try {
            return new GetActivationCodeResponse(this.activationCodeFinder.getActivationCodeByCid(new Cid(cid))).toResponseObject();
        } catch (ActivationCodeNotFoundException e) {
            LOGGER.error("activation code not found", e);
            throw new ActivationCodeDoesNotExistResponse();
        }
    }

    @DeleteMapping("/{cid}")
    public ActivationCodeDeletedResponse removeActivationCode(@PathVariable("cid") String cid) {
        try {
            this.activationCodeService.deleteCode(new Cid(cid));
            return new ActivationCodeDeletedResponse();
        } catch (ActivationCodeNotFoundException e) {
            LOGGER.error("activation code not found", e);
            throw new ActivationCodeDoesNotExistResponse();
        }
    }
}
