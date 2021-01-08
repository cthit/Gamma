package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.activationcode.response.ActivationCodeDeletedResponse;
import it.chalmers.gamma.activationcode.response.ActivationCodeDoesNotExistResponse;
import it.chalmers.gamma.activationcode.response.GetActivationCodeResponse;
import it.chalmers.gamma.activationcode.response.GetActivationCodeResponse.GetActivationCodeResponseObject;
import it.chalmers.gamma.activationcode.response.GetAllActivationCodesResponse;
import it.chalmers.gamma.activationcode.response.GetAllActivationCodesResponse.GetAllActivationCodesResponseObject;
import it.chalmers.gamma.activationcode.ActivationCodeService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/activation_codes")
public final class UserActivationCodeAdminController {

    private final ActivationCodeService activationCodeService;

    public UserActivationCodeAdminController(ActivationCodeService activationCodeService) {
        this.activationCodeService = activationCodeService;
    }

    @GetMapping()
    public GetAllActivationCodesResponseObject getAllActivationCodes() {
        return new GetAllActivationCodesResponse(
                this.activationCodeService.getAllActivationCodes()).toResponseObject();
    }

    @GetMapping("/{activationCode}")
    public GetActivationCodeResponseObject getActivationCode(
            @PathVariable("activationCode") String activationCode) {
        if (!this.activationCodeService.codeExists(activationCode)) {
            throw new ActivationCodeDoesNotExistResponse();
        }
        return new GetActivationCodeResponse(
                this.activationCodeService.getActivationCodeDTO(activationCode)).toResponseObject();
    }

    @DeleteMapping("/{activationCode}")
    public ActivationCodeDeletedResponse removeActivationCode(@PathVariable("activationCode") String activationCode) {
        if (!this.activationCodeService.codeExists(activationCode)) {
            throw new ActivationCodeDoesNotExistResponse();
        }
        if (!this.activationCodeService.deleteCode(activationCode)) {
            throw new ActivationCodeDoesNotExistResponse();
        }
        return new ActivationCodeDeletedResponse();
    }
}
