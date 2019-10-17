package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class AuthorityNotFoundResponse extends CustomResponseStatusException {
    public AuthorityNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_NOT_FOUND");
    }
}
