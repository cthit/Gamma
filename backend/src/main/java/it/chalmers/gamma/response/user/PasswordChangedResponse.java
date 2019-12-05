package it.chalmers.gamma.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PasswordChangedResponse extends ResponseEntity<String> {
    public PasswordChangedResponse() {
        super("PASSWORD_CHANGED", HttpStatus.ACCEPTED);
    }
}
