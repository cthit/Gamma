package it.chalmers.gamma.response.user;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class UserNotFoundResponse extends CustomResponseStatusException {
    public UserNotFoundResponse() {
        super(HttpStatus.NOT_FOUND, "NO_USER_FOUND");
    }
}
