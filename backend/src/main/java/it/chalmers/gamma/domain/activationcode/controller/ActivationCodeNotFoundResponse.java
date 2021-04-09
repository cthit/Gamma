package it.chalmers.gamma.domain.activationcode.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class ActivationCodeNotFoundResponse extends ErrorResponse {

    protected ActivationCodeNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
