package it.chalmers.gamma.domain.supergroup.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class SuperGroupAlreadyExistsResponse extends ErrorResponse {
    public SuperGroupAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
