package it.chalmers.gamma.domain.user.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PasswordTooShortResponse extends ErrorResponse {
    public PasswordTooShortResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
