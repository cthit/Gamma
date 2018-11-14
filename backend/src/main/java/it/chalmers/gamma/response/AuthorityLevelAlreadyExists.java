package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class AuthorityLevelAlreadyExists extends ResponseStatusException {
    public AuthorityLevelAlreadyExists() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_LEVEL_ALREADY_EXISTS");
    }
}
