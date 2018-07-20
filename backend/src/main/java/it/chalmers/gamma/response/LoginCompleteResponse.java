package it.chalmers.gamma.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LoginCompleteResponse extends ResponseEntity<String>{

    public LoginCompleteResponse(String jwt){
        super(jwt,  HttpStatus.ACCEPTED);
    }

}
