package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserDeletedResponse extends ResponseEntity<String> {
    public UserDeletedResponse() {
        super("USER_DELETED", HttpStatus.ACCEPTED);
    }
}
