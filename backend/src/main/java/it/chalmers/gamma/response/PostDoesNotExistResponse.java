package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostDoesNotExistResponse extends ResponseEntity<String>{
    public PostDoesNotExistResponse() {
        super("POST_DOES_NOT_EXIST", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
