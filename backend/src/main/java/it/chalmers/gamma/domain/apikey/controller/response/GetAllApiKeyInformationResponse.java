package it.chalmers.gamma.domain.apikey.controller.response;

import java.util.List;

import it.chalmers.gamma.domain.apikey.data.dto.ApiKeyInformationDTO;

public class GetAllApiKeyInformationResponse {

    public final List<ApiKeyInformationDTO> apiKeys;

    public GetAllApiKeyInformationResponse(List<ApiKeyInformationDTO> apiKeys) {
        this.apiKeys = apiKeys;
    }

}
