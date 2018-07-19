package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MissingRequiredFieldResponse extends ResponseEntity<String>{
    public MissingRequiredFieldResponse(String missingField) {
        super("MISSING_FIELD: " + missingField, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
