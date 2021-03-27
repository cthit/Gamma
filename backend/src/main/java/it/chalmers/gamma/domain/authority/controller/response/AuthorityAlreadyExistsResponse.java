package it.chalmers.gamma.domain.authority.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthorityAlreadyExistsResponse extends ErrorResponse {
    public AuthorityAlreadyExistsResponse() {
        super(HttpStatus.CONFLICT);
    }
}
