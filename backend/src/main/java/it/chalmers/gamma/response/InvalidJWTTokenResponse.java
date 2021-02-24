package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class InvalidJWTTokenResponse extends ErrorResponse {

    public InvalidJWTTokenResponse() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
