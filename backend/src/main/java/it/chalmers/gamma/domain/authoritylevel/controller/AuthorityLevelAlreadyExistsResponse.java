package it.chalmers.gamma.domain.authoritylevel.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthorityLevelAlreadyExistsResponse extends ErrorResponse {
    protected AuthorityLevelAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
