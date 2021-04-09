package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class UserNotFoundResponse extends ErrorResponse {
    protected UserNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
