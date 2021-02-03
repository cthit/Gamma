package it.chalmers.gamma.authoritylevel.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class AuthorityLevelAlreadyExistsResponse extends CustomResponseStatusException {
    public AuthorityLevelAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_LEVEL_ALREADY_EXISTS");
    }
}
