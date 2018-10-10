package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistsResponse extends ResponseStatusException {
    public UserAlreadyExistsResponse(){
        super(HttpStatus.UNPROCESSABLE_ENTITY, "USER_ALREADY_REGISTERED");
    }
}
