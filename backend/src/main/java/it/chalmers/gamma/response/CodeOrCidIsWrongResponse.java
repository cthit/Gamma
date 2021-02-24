package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class CodeOrCidIsWrongResponse extends ErrorResponse {
    public CodeOrCidIsWrongResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
