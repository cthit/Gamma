package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EditedClientResponse extends ResponseEntity<String> {

    public EditedClientResponse() {
        super("EDITED_CLIENT_RESPONSE", HttpStatus.ACCEPTED);
    }
}
