package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorityLevelAlreadyExists extends CustomResponseStatusException {
    public AuthorityLevelAlreadyExists() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_LEVEL_ALREADY_EXISTS");
    }
}
