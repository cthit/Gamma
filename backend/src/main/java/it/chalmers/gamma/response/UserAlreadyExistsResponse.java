package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserAlreadyExistsResponse extends ResponseEntity<String> {
    public UserAlreadyExistsResponse(){
        super("USER_ALREADY_REGISTERED", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
