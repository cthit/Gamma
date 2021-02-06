package it.chalmers.gamma.client.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClientRemovedResponse extends ResponseEntity<String> {
    public ClientRemovedResponse() {
        super("REMOVED_CLIENT", HttpStatus.OK);
    }
}
