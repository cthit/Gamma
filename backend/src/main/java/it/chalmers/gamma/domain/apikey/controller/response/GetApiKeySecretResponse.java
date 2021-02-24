package it.chalmers.gamma.domain.apikey.controller.response;

import it.chalmers.gamma.domain.apikey.domain.ApiKeyToken;

public class GetApiKeySecretResponse {

    public final ApiKeyToken secret;

    public GetApiKeySecretResponse(ApiKeyToken secret) {
        this.secret = secret;
    }

}
