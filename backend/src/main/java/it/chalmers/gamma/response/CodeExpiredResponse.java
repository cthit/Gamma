package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CodeExpiredResponse extends ResponseEntity<String> {
    public CodeExpiredResponse() {
        super("CODE_EXPIRED", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
