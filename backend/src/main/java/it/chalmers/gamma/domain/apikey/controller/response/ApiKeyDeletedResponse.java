package it.chalmers.gamma.domain.apikey.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiKeyDeletedResponse extends ResponseEntity<String> {

    public ApiKeyDeletedResponse() {
        super("API_KEY_DELETED", HttpStatus.ACCEPTED);
    }
}
