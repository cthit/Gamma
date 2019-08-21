package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WhitelistAddedResponse extends ResponseEntity<String> {

    public WhitelistAddedResponse(int nAdded, int nTotal) {
        super("Added" + nAdded + "of requested" + nTotal, HttpStatus.ACCEPTED);
    }

}
