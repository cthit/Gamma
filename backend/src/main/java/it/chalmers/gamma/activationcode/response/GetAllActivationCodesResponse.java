package it.chalmers.gamma.activationcode.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.activationcode.ActivationCodeDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllActivationCodesResponse {
    @JsonValue
    private final List<ActivationCodeDTO> activationCodes;

    public GetAllActivationCodesResponse(List<ActivationCodeDTO> activationCodes) {
        this.activationCodes = activationCodes;
    }

    public List<ActivationCodeDTO> getActivationCodes() {
        return this.activationCodes;
    }

    public GetAllActivationCodesResponseObject toResponseObject() {
        return new GetAllActivationCodesResponseObject(this);
    }

    public static class GetAllActivationCodesResponseObject extends ResponseEntity<GetAllActivationCodesResponse> {
        GetAllActivationCodesResponseObject(GetAllActivationCodesResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
