package it.chalmers.gamma.response.activation_code;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ActivationCodeAddedResonse extends ResponseEntity<String> {
    public ActivationCodeAddedResonse() {
        super("ACTIVATION_CODE_ADDED", HttpStatus.ACCEPTED);
    }
}