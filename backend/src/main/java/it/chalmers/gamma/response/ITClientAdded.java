package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ITClientAdded extends ResponseEntity<String> {
    public ITClientAdded() {
        super("CLIENT_ADDED", HttpStatus.ACCEPTED);
    }
}
