package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class InvalidJWTTokenResponse extends ResponseEntity<String> {

    public InvalidJWTTokenResponse(){
        super("INVALID_JWT_TOKEN", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
