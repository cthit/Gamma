package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.response.activation_code.ActivationCodeDeletedResponse;
import it.chalmers.gamma.response.activation_code.ActivationCodeDoesNotExistResponse;
import it.chalmers.gamma.response.activation_code.GetActivationCodeResponse;
import it.chalmers.gamma.response.activation_code.GetActivationCodeResponse.GetActivationCodeResponseObject;
import it.chalmers.gamma.response.activation_code.GetAllActivationCodesResponse;
import it.chalmers.gamma.response.activation_code.GetAllActivationCodesResponse.GetAllActivationCodesResponseObject;
import it.chalmers.gamma.service.ActivationCodeService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/activation_codes")
public final class UserActivationCodeAdminController {

    private final ActivationCodeService activationCodeService;

    public UserActivationCodeAdminController(ActivationCodeService activationCodeService) {
        this.activationCodeService = activationCodeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public GetAllActivationCodesResponseObject getAllActivationCodes() {
        return new GetAllActivationCodesResponse(
                this.activationCodeService.getAllActivationCodes()).getResponseObject();
    }

    @RequestMapping(value = "/{activationCode}", method = RequestMethod.GET)
    public GetActivationCodeResponseObject getActivationCode(
            @PathVariable("activationCode") String activationCode) {
        if (!this.activationCodeService.codeExists(UUID.fromString(activationCode))) {
            throw new ActivationCodeDoesNotExistResponse();
        }
        return new GetActivationCodeResponse(
                this.activationCodeService.getActivationCode(UUID.fromString(activationCode))).getResponseObject();
    }

    @RequestMapping(value = "/{activationCode}", method = RequestMethod.DELETE)
    public ActivationCodeDeletedResponse removeActivationCode(@PathVariable("activationCode") String activationCode) {
        if (!this.activationCodeService.codeExists(UUID.fromString(activationCode))) {
            throw new ActivationCodeDoesNotExistResponse();
        }
        this.activationCodeService.deleteCode(UUID.fromString(activationCode));
        return new ActivationCodeDeletedResponse();
    }
}
