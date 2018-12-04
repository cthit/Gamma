package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CIDAlreadyWhitelistedResponse extends ResponseStatusException {

    public CIDAlreadyWhitelistedResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CID_ALREADY_FOUND");
    }
}
