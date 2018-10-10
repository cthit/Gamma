package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class PostAlreadyExistsResponse extends ResponseStatusException {

    public PostAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "POST_ALREADY_EXISTS");
    }
}
