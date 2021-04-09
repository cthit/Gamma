package it.chalmers.gamma.domain.activationcode.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.activationcode.service.ActivationCodeDTO;
import java.util.List;

public class GetAllActivationCodeResponse {

    @JsonValue
    private final List<ActivationCodeDTO> activationCodes;

    public GetAllActivationCodeResponse(List<ActivationCodeDTO> activationCodes) {
        this.activationCodes = activationCodes;
    }

}
