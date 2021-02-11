package it.chalmers.gamma.domain.authority.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class AuthorityAlreadyExistsResponse extends CustomResponseStatusException {

    public AuthorityAlreadyExistsResponse() {
        super(HttpStatus.CONFLICT, "Authority already exists");
    }
}
