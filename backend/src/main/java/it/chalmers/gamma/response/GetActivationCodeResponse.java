package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.ActivationCode;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetActivationCodeResponse extends ResponseEntity<ActivationCode> {

    public GetActivationCodeResponse(ActivationCode activationCode) {
        super(activationCode, HttpStatus.OK);
    }
}
