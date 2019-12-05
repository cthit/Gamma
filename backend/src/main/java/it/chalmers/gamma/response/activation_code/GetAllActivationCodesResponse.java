package it.chalmers.gamma.response.activation_code;

import it.chalmers.gamma.domain.dto.user.ActivationCodeDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllActivationCodesResponse {
    private final List<ActivationCodeDTO> activationCode;

    public GetAllActivationCodesResponse(List<ActivationCodeDTO> activationCode) {
        this.activationCode = activationCode;
    }

    public List<ActivationCodeDTO> getActivationCode() {
        return activationCode;
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
