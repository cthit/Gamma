package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CodeExpiredResponse extends CustomResponseStatusException {
    public CodeExpiredResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CODE_EXPIRED");
    }
}
