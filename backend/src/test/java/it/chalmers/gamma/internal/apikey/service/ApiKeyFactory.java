package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.domain.ApiKey;
import it.chalmers.gamma.domain.ApiKeyToken;

public class ApiKeyFactory {

    private ApiKeyFactory() { }

    public static ApiKeyEntity create(ApiKey apiKey, ApiKeyToken apiKeyToken) {
        return new ApiKeyEntity(apiKey, apiKeyToken);
    }

}
