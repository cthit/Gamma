package it.chalmers.gamma.client.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class ClientDoesNotExistException extends CustomResponseStatusException {
    public ClientDoesNotExistException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "NO_SUCH_CLIENT_EXISTS");
    }
}
