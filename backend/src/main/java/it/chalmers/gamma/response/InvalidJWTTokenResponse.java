package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class InvalidJWTTokenResponse extends CustomResponseStatusException {

    public InvalidJWTTokenResponse() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "INVALID_JWT_TOKEN");
    }

}
