package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class AuthorityLevelNotFoundResponse extends CustomResponseStatusException {
    public AuthorityLevelNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_LEVEL_NOT_FOUND");
    }
}
