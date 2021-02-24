package it.chalmers.gamma.domain.supergroup.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class SuperGroupDoesNotExistResponse extends ErrorResponse {
    public SuperGroupDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
