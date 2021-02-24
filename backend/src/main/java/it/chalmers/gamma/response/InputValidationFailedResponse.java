package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InputValidationFailedResponse extends ResponseStatusException {

    public InputValidationFailedResponse(String errors) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, errors);
    }
}
