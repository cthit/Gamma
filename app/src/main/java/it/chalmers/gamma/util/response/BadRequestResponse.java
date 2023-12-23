package it.chalmers.gamma.util.response;

import org.springframework.http.HttpStatus;

public class BadRequestResponse extends ErrorResponse {
    public BadRequestResponse() {
        super(HttpStatus.BAD_REQUEST);
    }
}
