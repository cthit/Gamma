package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostDeletedResponse extends ResponseEntity<String> {

    public PostDeletedResponse() {
        super("POST_DELETED", HttpStatus.OK);
    }
}
