package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.domain.apikey.service.ApiKey;
import it.chalmers.gamma.domain.apikey.service.ApiKeyDTO;

public class ApiKeyFactory {

    private ApiKeyFactory() { }

    public static ApiKey create(ApiKeyDTO apiKey) {
        return new ApiKey(apiKey);
    }

}
