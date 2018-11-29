package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IncorrectCidOrPasswordResponse extends ResponseStatusException {
    public IncorrectCidOrPasswordResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "INCORRECT_CID_OR_PASSWORD");
    }

}
