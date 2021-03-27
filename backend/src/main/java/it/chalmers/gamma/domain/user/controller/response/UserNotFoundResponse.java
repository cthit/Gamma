package it.chalmers.gamma.domain.user.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class UserNotFoundResponse extends ErrorResponse {
    public UserNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }
}
