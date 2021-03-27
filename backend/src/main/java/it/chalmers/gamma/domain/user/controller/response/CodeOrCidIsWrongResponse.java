package it.chalmers.gamma.domain.user.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class CodeOrCidIsWrongResponse extends ErrorResponse {
    public CodeOrCidIsWrongResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
