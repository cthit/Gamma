package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostAlreadyExistsResponse extends ResponseEntity<String>{

    public PostAlreadyExistsResponse() {
        super("POST_ALREADY_EXISTS", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
