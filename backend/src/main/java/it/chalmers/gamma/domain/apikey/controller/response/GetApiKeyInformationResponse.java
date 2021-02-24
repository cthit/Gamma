package it.chalmers.gamma.domain.apikey.controller.response;

import it.chalmers.gamma.domain.apikey.data.dto.ApiKeyInformationDTO;

public class GetApiKeyInformationResponse {

    public final ApiKeyInformationDTO apiKey;

    public GetApiKeyInformationResponse(ApiKeyInformationDTO apiKey) {
        this.apiKey = apiKey;
    }

}
