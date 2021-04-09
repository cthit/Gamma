package it.chalmers.gamma.domain.supergroup.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class SuperGroupAlreadyExistsResponse extends ErrorResponse {
    protected SuperGroupAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
