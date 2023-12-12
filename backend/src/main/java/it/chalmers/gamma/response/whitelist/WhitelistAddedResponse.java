package it.chalmers.gamma.response.whitelist;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WhitelistAddedResponse extends ResponseEntity<String> {

    public WhitelistAddedResponse(int numAdded, int numTotal) {
        super("Added" + numAdded + "of requested" + numTotal, HttpStatus.ACCEPTED);
    }

}
