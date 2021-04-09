package it.chalmers.gamma.domain.apikey.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.apikey.service.ApiKeyInformationDTO;

public class GetAllApiKeyInformationResponse {

    @JsonValue
    private final List<ApiKeyInformationDTO> apiKeys;

    protected GetAllApiKeyInformationResponse(List<ApiKeyInformationDTO> apiKeys) {
        this.apiKeys = apiKeys;
    }

}
