package it.chalmers.gamma.domain.authority.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthorityNotFoundResponse extends ErrorResponse {
    public AuthorityNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
