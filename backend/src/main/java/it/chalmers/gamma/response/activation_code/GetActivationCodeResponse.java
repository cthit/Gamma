package it.chalmers.gamma.response.activation_code;

import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetActivationCodeResponse {
   private final ActivationCodeDTO activationCode;

    public GetActivationCodeResponse(ActivationCodeDTO activationCode) {
        this.activationCode = activationCode;
    }

    public ActivationCodeDTO getActivationCode() {
        return activationCode;
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
