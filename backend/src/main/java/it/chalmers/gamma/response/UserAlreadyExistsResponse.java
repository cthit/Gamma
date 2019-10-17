package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsResponse extends CustomResponseStatusException {
    public UserAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "USER_ALREADY_REGISTERED");
    }
}
