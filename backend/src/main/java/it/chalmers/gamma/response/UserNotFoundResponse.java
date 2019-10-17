package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class UserNotFoundResponse extends CustomResponseStatusException {
    public UserNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "NO_USER_FOUND");
    }
}
