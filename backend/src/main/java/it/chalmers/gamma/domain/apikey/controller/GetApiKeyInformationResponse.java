package it.chalmers.gamma.domain.apikey.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.apikey.service.ApiKeyInformationDTO;

public class GetApiKeyInformationResponse {

    @JsonValue
    private final ApiKeyInformationDTO apiKey;

    protected GetApiKeyInformationResponse(ApiKeyInformationDTO apiKey) {
        this.apiKey = apiKey;
    }

}
