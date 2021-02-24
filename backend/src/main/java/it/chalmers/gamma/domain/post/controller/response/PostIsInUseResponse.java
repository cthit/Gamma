package it.chalmers.gamma.domain.post.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PostIsInUseResponse extends ErrorResponse {
    public PostIsInUseResponse() {
        super(HttpStatus.NOT_ACCEPTABLE);
    }
}
