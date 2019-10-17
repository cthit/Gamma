package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ValidJwtResponse extends ResponseEntity<Boolean> {
    public ValidJwtResponse(boolean isValid) {
        super(isValid, HttpStatus.ACCEPTED);
    }
}
