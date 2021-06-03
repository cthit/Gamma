package it.chalmers.gamma.internal.activationcode.controller;

import it.chalmers.gamma.internal.activationcode.service.ActivationCodeDTO;
import it.chalmers.gamma.internal.activationcode.service.ActivationCodeService;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/activation_codes")
public final class ActivationCodeAdminController {

    private final ActivationCodeService activationCodeService;

    public ActivationCodeAdminController(ActivationCodeService activationCodeService) {
        this.activationCodeService = activationCodeService;
    }

    @GetMapping()
    public List<ActivationCodeDTO> getAllActivationCodes() {
        return this.activationCodeService.getAll();
    }

    @DeleteMapping("/{cid}")
    public ActivationCodeDeletedResponse removeActivationCode(@PathVariable("cid") Cid cid) {
        try {
            this.activationCodeService.delete(cid);
            return new ActivationCodeDeletedResponse();
        } catch (ActivationCodeService.ActivationCodeNotFoundException e) {
            throw new ActivationCodeNotFoundResponse();
        }
    }

    private static class ActivationCodeDeletedResponse extends SuccessResponse { }

    private static class ActivationCodeNotFoundResponse extends ErrorResponse {
        private ActivationCodeNotFoundResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
