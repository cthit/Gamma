package it.chalmers.gamma.util.response;

import org.springframework.http.HttpStatus;

public abstract class NotFoundResponse extends ErrorResponse {
    public NotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
