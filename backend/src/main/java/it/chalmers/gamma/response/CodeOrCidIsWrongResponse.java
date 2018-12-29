package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CodeOrCidIsWrongResponse extends ResponseStatusException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

    public CodeOrCidIsWrongResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CODE_OR_CID_IS_WRONG");
    }

}
