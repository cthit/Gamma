package it.chalmers.gamma.authority.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthorityRemovedResponse extends ResponseEntity<String> {

    public AuthorityRemovedResponse() {
        super("REMOVED_AUTHORITY", HttpStatus.ACCEPTED);
    }
}
