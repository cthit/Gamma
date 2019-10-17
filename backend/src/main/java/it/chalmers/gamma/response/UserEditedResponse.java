package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserEditedResponse extends ResponseEntity<String> {
    public UserEditedResponse() {
        super("USER_EDITED", HttpStatus.ACCEPTED);
    }
}
