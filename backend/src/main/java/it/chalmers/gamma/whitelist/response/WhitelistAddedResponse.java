package it.chalmers.gamma.whitelist.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WhitelistAddedResponse extends ResponseEntity<String> {

    public WhitelistAddedResponse() {
        super(HttpStatus.OK);
    }
}
