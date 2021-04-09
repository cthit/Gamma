package it.chalmers.gamma.domain.post.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class PostDoesNotExistResponse extends ErrorResponse {
    protected PostDoesNotExistResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
