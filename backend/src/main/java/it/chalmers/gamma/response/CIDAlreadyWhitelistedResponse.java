package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class CIDAlreadyWhitelistedResponse extends ResponseStatusException{
    public CIDAlreadyWhitelistedResponse(){//"cid already exists in the whitelist table"
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CID_ALREADY_FOUND");
    }
}
