package it.chalmers.gamma.domain.apikey.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.apikey.data.ApiKeyDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetApiKeyResponse {
    @JsonUnwrapped
    private final ApiKeyDTO apiKey;


    public GetApiKeyResponse(ApiKeyDTO apiKey) {
        this.apiKey = apiKey;
    }

    public ApiKeyDTO getApiKey() {
        return this.apiKey;
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
