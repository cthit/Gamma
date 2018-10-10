package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class MissingRequiredFieldResponse extends ResponseStatusException {
    public MissingRequiredFieldResponse(String missingField) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "MISSING_FIELD: " + missingField);
    }
}
