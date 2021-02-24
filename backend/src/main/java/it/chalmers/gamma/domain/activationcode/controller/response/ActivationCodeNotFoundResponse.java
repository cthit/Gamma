package it.chalmers.gamma.domain.activationcode.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class ActivationCodeNotFoundResponse extends ErrorResponse {

    public ActivationCodeNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
