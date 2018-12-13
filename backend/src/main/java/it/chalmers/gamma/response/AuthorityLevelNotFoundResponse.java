package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthorityLevelNotFoundResponse extends CustomResponseStatusException {
    public AuthorityLevelNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_LEVEL_NOT_FOUND");
    }
}
