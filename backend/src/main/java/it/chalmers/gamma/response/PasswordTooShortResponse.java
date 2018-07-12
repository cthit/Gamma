package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PasswordTooShortResponse extends ResponseEntity<String> {
    public PasswordTooShortResponse(){
        super("TOO_SHORT_PASSWORD", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
