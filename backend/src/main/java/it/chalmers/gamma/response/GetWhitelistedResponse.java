package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetWhitelistedResponse extends ResponseEntity<List<String>> {
    public GetWhitelistedResponse(List<String> whitelist){
        super(whitelist, HttpStatus.ACCEPTED);
    }
}
