package it.chalmers.gamma.domain.authoritylevel.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthorityLevelAlreadyExistsResponse extends ErrorResponse {
    public AuthorityLevelAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
