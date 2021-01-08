package it.chalmers.gamma.post.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostEditedResponse extends ResponseEntity<String> {
    public PostEditedResponse() {
        super("POST_EDITED", HttpStatus.ACCEPTED);
    }
}
