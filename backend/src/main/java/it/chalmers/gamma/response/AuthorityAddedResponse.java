package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthorityAddedResponse extends ResponseEntity<String> {
    public AuthorityAddedResponse() {
        super("AUTHORITY_ADDED", HttpStatus.ACCEPTED);
    }
}
