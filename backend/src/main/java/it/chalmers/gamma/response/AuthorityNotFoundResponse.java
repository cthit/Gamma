package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorityNotFoundResponse extends CustomResponseStatusException {
    public AuthorityNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_NOT_FOUND");
    }
}
