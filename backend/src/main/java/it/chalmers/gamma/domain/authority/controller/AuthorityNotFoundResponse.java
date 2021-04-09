package it.chalmers.gamma.domain.authority.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthorityNotFoundResponse extends ErrorResponse {
    protected AuthorityNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
