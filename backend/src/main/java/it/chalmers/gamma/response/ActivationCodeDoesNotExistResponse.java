package it.chalmers.gamma.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class ActivationCodeDoesNotExistResponse extends CustomResponseStatusException {

    public ActivationCodeDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "ACTIVATION_CODE_DOES_NOT_EXIST");
    }
}
