package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchClientExistsResponse extends ResponseStatusException {
    public NoSuchClientExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "NO_SUCH_CLIENT_EXISTS");
    }
}
