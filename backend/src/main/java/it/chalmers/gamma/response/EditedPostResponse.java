package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EditedPostResponse extends ResponseEntity<String> {
    public EditedPostResponse() {
        super("POST_EDITED", HttpStatus.ACCEPTED);
    }
}
