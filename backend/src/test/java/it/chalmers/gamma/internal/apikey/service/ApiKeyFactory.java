package it.chalmers.gamma.internal.apikey.service;

public class ApiKeyFactory {

    private ApiKeyFactory() { }

    public static ApiKey create(ApiKeyDTO apiKey) {
        return new ApiKey(apiKey);
    }

}
