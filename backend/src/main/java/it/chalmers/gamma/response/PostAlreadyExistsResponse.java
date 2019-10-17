package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class PostAlreadyExistsResponse extends CustomResponseStatusException {

    public PostAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "POST_ALREADY_EXISTS");
    }
}
