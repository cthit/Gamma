package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class CodeExpiredResponse extends CustomResponseStatusException {
    public CodeExpiredResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CODE_EXPIRED");
    }
}
