package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CIDAlreadyWhitelistedResponse extends ResponseEntity<String> {
    public CIDAlreadyWhitelistedResponse(){//"cid already exists in the whitelist table"
        super( "CID_ALREADY_FOUND" , HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
