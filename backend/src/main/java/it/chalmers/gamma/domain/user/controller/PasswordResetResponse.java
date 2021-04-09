package it.chalmers.gamma.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PasswordResetResponse extends ResponseEntity<String> {

    public PasswordResetResponse() {
        super("PASSWORD_RESET_LINK_SENT", HttpStatus.ACCEPTED);
    }
}
