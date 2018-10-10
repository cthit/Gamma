package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class PostDoesNotExistResponse extends ResponseStatusException {
    public PostDoesNotExistResponse() {
        super(HttpStatus.NOT_FOUND, "POST_DOES_NOT_EXIST");
    }
}
