package it.chalmers.gamma.domain.authority.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthorityAlreadyExistsResponse extends ErrorResponse {
    protected AuthorityAlreadyExistsResponse() {
        super(HttpStatus.CONFLICT);
    }
}
