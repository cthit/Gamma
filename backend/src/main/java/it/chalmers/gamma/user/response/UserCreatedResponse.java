package it.chalmers.gamma.user.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserCreatedResponse extends ResponseEntity<String> {

    public UserCreatedResponse() {
        super("USER_CREATED", HttpStatus.ACCEPTED);
    }

}
