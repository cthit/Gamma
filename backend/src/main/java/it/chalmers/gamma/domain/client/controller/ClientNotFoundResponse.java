package it.chalmers.gamma.domain.client.controller;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

//TODO Not make it public??
public class ClientNotFoundResponse extends ErrorResponse {
    public ClientNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
