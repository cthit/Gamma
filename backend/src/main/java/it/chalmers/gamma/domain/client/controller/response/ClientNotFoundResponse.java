package it.chalmers.gamma.domain.client.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class ClientNotFoundResponse extends ErrorResponse {
    public ClientNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
