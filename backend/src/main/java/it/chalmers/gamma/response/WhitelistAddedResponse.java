package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WhitelistAddedResponse extends ResponseEntity<String> {

    public WhitelistAddedResponse(){
        super("USER_ADDED", HttpStatus.ACCEPTED);
    }

}
