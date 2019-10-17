package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class MissingRequiredFieldResponse extends CustomResponseStatusException {
    public MissingRequiredFieldResponse(String missingField) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "MISSING_FIELD: " + missingField);
    }
}
