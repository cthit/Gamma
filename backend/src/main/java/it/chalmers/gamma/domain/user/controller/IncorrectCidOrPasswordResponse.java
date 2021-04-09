package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class IncorrectCidOrPasswordResponse extends ErrorResponse {
    public IncorrectCidOrPasswordResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
