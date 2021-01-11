package it.chalmers.gamma.authority.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthorityLevelRemovedResponse extends ResponseEntity<String> {
    public AuthorityLevelRemovedResponse() {
        super("AUTHORITY_LEVEL_REMOVED", HttpStatus.ACCEPTED);
    }
}