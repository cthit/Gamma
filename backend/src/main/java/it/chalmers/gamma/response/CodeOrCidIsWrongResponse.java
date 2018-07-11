package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CodeOrCidIsWrongResponse extends ResponseEntity<String> {
    public CodeOrCidIsWrongResponse(){
        super("CODE_OR_CID_IS_WRONG", HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
