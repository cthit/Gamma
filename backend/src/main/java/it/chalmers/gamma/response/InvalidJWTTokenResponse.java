package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class InvalidJWTTokenResponse extends ResponseStatusException {

    public InvalidJWTTokenResponse(){
        super( HttpStatus.INTERNAL_SERVER_ERROR, "INVALID_JWT_TOKEN");
    }

}
