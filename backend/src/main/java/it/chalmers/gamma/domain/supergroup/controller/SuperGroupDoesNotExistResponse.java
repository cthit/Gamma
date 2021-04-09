package it.chalmers.gamma.domain.supergroup.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class SuperGroupDoesNotExistResponse extends ErrorResponse {
    protected SuperGroupDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
