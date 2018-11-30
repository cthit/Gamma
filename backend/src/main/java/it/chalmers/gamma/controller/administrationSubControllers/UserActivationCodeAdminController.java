package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.ActivationCode;
import it.chalmers.gamma.response.ActivationCodeDeletedResponse;
import it.chalmers.gamma.response.GetAllActivationCodesResponse;
import it.chalmers.gamma.service.ActivationCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/activation_codes")
public class UserActivationCodeAdminController {
    private ActivationCodeService activationCodeService;

    public UserActivationCodeAdminController(ActivationCodeService activationCodeService){
        this.activationCodeService = activationCodeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ActivationCode>> getAllActivationCodes(){
        return new GetAllActivationCodesResponse(activationCodeService.getAllActivationCodes());
    }
    @RequestMapping(value = "/{activationCode}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeActivationCode(@PathVariable("activationCode") String activationCode){
        if(!activationCodeService.codeExists(UUID.fromString(activationCode))){
            return new ActivationCodeDeletedResponse();
        }
        activationCodeService.deleteCode(UUID.fromString(activationCode));
        return new ActivationCodeDeletedResponse();
    }
}
