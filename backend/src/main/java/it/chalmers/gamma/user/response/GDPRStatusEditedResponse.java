package it.chalmers.gamma.user.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GDPRStatusEditedResponse extends ResponseEntity<String> {

    public GDPRStatusEditedResponse() {
        super("GDPREdited", HttpStatus.ACCEPTED);
    }
}
