package it.chalmers.gamma.domain.user.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsResponse extends ErrorResponse {
    public UserAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
