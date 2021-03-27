package it.chalmers.gamma.domain.authoritylevel.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthorityLevelNotFoundResponse extends ErrorResponse {
    public AuthorityLevelNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
