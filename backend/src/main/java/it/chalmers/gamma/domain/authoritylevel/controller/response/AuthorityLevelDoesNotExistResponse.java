package it.chalmers.gamma.domain.authoritylevel.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class AuthorityLevelDoesNotExistResponse extends CustomResponseStatusException {
    public AuthorityLevelDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_LEVEL_NOT_FOUND");
    }
}
