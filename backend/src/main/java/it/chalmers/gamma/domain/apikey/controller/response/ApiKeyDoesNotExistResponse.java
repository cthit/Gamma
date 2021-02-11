package it.chalmers.gamma.domain.apikey.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class ApiKeyDoesNotExistResponse extends CustomResponseStatusException {

    public ApiKeyDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "API_KEY_DOES_NOT_EXIST");
    }
}
