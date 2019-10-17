package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class PasswordTooShortResponse extends CustomResponseStatusException {
    public PasswordTooShortResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "TOO_SHORT_PASSWORD");
    }
}
