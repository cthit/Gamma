package it.chalmers.gamma.domain.client.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class ClientDoesNotExistResponse extends CustomResponseStatusException {
    public ClientDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "NO_SUCH_CLIENT_EXISTS");
    }
}
