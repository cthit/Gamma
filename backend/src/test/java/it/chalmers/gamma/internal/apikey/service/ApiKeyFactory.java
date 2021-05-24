package it.chalmers.gamma.internal.apikey.service;

public class ApiKeyFactory {

    private ApiKeyFactory() { }

    public static ApiKeyEntity create(ApiKeyDTO apiKey) {
        return new ApiKeyEntity(apiKey);
    }

}
