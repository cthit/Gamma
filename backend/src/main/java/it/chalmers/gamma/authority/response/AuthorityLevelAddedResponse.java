package it.chalmers.gamma.authority.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthorityLevelAddedResponse extends ResponseEntity<String> {
    public AuthorityLevelAddedResponse() {
        super("AUTHORITY_LEVEL_ADDED", HttpStatus.ACCEPTED);
    }
}
