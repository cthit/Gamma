package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class CodeExpiredResponse extends ResponseStatusException{
    public CodeExpiredResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CODE_EXPIRED");
    }
}
