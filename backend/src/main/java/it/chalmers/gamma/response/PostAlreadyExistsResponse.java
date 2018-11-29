package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PostAlreadyExistsResponse extends ResponseStatusException {

    public PostAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "POST_ALREADY_EXISTS");
    }
}
