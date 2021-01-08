package it.chalmers.gamma.post.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostCreatedResponse extends ResponseEntity<String> {
    public PostCreatedResponse() {
        super("POST_CREATED", HttpStatus.ACCEPTED);
    }
}
