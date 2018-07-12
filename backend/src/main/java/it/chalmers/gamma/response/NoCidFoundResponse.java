package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class NoCidFoundResponse extends ResponseEntity<String> {
    public NoCidFoundResponse() {
        super("NO_CID_FOUND", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
