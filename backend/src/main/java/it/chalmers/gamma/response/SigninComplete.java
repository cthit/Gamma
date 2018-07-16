package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SigninComplete extends ResponseEntity<String>{

    public SigninComplete(String token){
        super(token, HttpStatus.ACCEPTED);
    }

}
