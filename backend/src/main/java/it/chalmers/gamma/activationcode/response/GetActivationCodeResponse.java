package it.chalmers.gamma.activationcode.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.activationcode.ActivationCodeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetActivationCodeResponse {
    @JsonUnwrapped
    private final ActivationCodeDTO activationCode;

    public GetActivationCodeResponse(ActivationCodeDTO activationCode) {
        this.activationCode = activationCode;
    }

    public ActivationCodeDTO getActivationCode() {
        return this.activationCode;
    }

    public GetActivationCodeResponseObject toResponseObject() {
        return new GetActivationCodeResponseObject(this);
    }

    public static class GetActivationCodeResponseObject extends ResponseEntity<GetActivationCodeResponse> {
        GetActivationCodeResponseObject(GetActivationCodeResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
