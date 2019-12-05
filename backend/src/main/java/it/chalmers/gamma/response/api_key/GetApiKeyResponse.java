package it.chalmers.gamma.response.api_key;

import it.chalmers.gamma.domain.dto.access.ApiKeyDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetApiKeyResponse {
    private final ApiKeyDTO apiKey;


    public GetApiKeyResponse(ApiKeyDTO apiKey) {
        this.apiKey = apiKey;
    }

    public ApiKeyDTO getApiKey() {
        return apiKey;
    }

    public GetApiKeyResponseObject toResponseObject() {
        return new GetApiKeyResponseObject(this);
    }

    public static class GetApiKeyResponseObject extends ResponseEntity<GetApiKeyResponse> {

        GetApiKeyResponseObject(GetApiKeyResponse response) {
            super(response, HttpStatus.OK);
        }
    }
}
