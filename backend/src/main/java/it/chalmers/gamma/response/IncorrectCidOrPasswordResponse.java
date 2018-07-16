package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class IncorrectCidOrPasswordResponse extends ResponseEntity<String>{
    public IncorrectCidOrPasswordResponse(){
        super("INCORRECT_CID", HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
