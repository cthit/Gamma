package it.chalmers.gamma.domain.user.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class IncorrectCidOrPasswordResponse extends ErrorResponse {
    public IncorrectCidOrPasswordResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
