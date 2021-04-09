package it.chalmers.gamma.domain.apikey.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.apikey.service.ApiKeyToken;

public class GetApiKeySecretResponse {

    @JsonValue
    private final ApiKeyToken secret;

    protected GetApiKeySecretResponse(ApiKeyToken secret) {
        this.secret = secret;
    }

}
