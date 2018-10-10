package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class PasswordTooShortResponse extends ResponseStatusException {
    public PasswordTooShortResponse(){
        super(HttpStatus.UNPROCESSABLE_ENTITY, "TOO_SHORT_PASSWORD");
    }
}
