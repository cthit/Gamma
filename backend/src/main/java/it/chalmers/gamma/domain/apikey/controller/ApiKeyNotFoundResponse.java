package it.chalmers.gamma.domain.apikey.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class ApiKeyNotFoundResponse extends ErrorResponse {
    protected ApiKeyNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
