package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class AuthorityLevelAlreadyExists extends CustomResponseStatusException {
    public AuthorityLevelAlreadyExists() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_LEVEL_ALREADY_EXISTS");
    }
}
