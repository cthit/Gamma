package it.chalmers.gamma.util.response;

import org.springframework.http.HttpStatus;

public abstract class AlreadyExistsResponse extends ErrorResponse {
    public AlreadyExistsResponse() {
        super(HttpStatus.CONFLICT);
    }
}
