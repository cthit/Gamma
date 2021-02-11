package it.chalmers.gamma.domain.user.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsResponse extends CustomResponseStatusException {
    public UserAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "USER_ALREADY_REGISTERED");
    }
}
