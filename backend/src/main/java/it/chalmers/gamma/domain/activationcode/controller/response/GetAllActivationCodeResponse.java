package it.chalmers.gamma.domain.activationcode.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.activationcode.data.dto.ActivationCodeDTO;
import java.util.List;

public class GetAllActivationCodeResponse {

    @JsonValue
    public final List<ActivationCodeDTO> activationCodes;

    public GetAllActivationCodeResponse(List<ActivationCodeDTO> activationCodes) {
        this.activationCodes = activationCodes;
    }

}
