package it.chalmers.gamma.domain.apikey.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class ApiKeyNotFoundResponse extends ErrorResponse {
    public ApiKeyNotFoundResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
