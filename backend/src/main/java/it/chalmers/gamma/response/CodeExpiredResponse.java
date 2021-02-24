package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class CodeExpiredResponse extends ErrorResponse {
    public CodeExpiredResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
