package it.chalmers.gamma.response.authority;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class AuthorityNotFoundResponse extends CustomResponseStatusException {
    public AuthorityNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "AUTHORITY_NOT_FOUND");
    }
}
