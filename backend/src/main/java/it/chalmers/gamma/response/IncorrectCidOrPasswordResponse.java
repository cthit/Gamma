package it.chalmers.delta.response;

import org.springframework.http.HttpStatus;

public class IncorrectCidOrPasswordResponse extends CustomResponseStatusException {
    public IncorrectCidOrPasswordResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "INCORRECT_CID_OR_PASSWORD");
    }

}
