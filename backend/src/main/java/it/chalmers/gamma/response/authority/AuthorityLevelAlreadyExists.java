package it.chalmers.gamma.response.authority;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class AuthorityLevelAlreadyExists extends CustomResponseStatusException {
    public AuthorityLevelAlreadyExists() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_LEVEL_ALREADY_EXISTS");
    }
}
