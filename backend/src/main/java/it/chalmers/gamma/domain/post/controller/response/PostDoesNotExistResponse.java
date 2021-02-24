package it.chalmers.gamma.domain.post.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PostDoesNotExistResponse extends ErrorResponse {
    public PostDoesNotExistResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
