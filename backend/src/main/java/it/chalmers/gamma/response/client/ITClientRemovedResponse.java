package it.chalmers.gamma.response.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ITClientRemovedResponse extends ResponseEntity<String> {
    public ITClientRemovedResponse() {
        super("REMOVED_CLIENT", HttpStatus.OK);
    }
}
