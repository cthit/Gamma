package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WhitelistAddedResponse extends ResponseEntity<String> {

    public WhitelistAddedResponse(int n_added, int n_total) {
        super("Added" + n_added + "of requested" + n_total, HttpStatus.ACCEPTED);
    }

}
