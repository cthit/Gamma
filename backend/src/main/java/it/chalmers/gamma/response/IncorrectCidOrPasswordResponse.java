package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IncorrectCidOrPasswordResponse extends CustomResponseStatusException {
    public IncorrectCidOrPasswordResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "INCORRECT_CID_OR_PASSWORD");
    }

}
