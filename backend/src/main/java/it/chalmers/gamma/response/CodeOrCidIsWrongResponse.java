package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class CodeOrCidIsWrongResponse extends ResponseStatusException {
    public CodeOrCidIsWrongResponse(){
        super(HttpStatus.UNPROCESSABLE_ENTITY, "CODE_OR_CID_IS_WRONG");
    }

}
