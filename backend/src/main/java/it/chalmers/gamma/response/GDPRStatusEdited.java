package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GDPRStatusEdited extends ResponseEntity<String> {

    public GDPRStatusEdited() {
        super("GDPREdited", HttpStatus.ACCEPTED);
    }
}
