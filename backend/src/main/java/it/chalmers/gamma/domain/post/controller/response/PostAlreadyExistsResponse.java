package it.chalmers.gamma.domain.post.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PostAlreadyExistsResponse extends ErrorResponse {

    public PostAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
