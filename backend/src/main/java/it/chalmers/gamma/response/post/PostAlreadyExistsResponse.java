package it.chalmers.gamma.response.post;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class PostAlreadyExistsResponse extends CustomResponseStatusException {

    public PostAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "POST_ALREADY_EXISTS");
    }
}
