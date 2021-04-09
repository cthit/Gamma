package it.chalmers.gamma.domain.authoritylevel.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthorityLevelNotFoundResponse extends ErrorResponse {
    protected AuthorityLevelNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
