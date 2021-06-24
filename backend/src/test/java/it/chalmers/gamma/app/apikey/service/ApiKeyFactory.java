package it.chalmers.gamma.app.apikey.service;

import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyToken;

public class ApiKeyFactory {

    private ApiKeyFactory() { }

    public static ApiKeyEntity create(ApiKey apiKey, ApiKeyToken apiKeyToken) {
        return new ApiKeyEntity(apiKey, apiKeyToken);
    }

}
