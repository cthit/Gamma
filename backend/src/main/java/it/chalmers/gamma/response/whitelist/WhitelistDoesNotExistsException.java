package it.chalmers.gamma.response.whitelist;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class WhitelistDoesNotExistsException extends CustomResponseStatusException {
    public WhitelistDoesNotExistsException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "WHITELIST_DOES_NOT_EXIST");
    }
}
