package it.chalmers.gamma.response.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ITClientDoesNotExistException extends ResponseStatusException {
    public ITClientDoesNotExistException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "NO_SUCH_CLIENT_EXISTS");
    }
}
