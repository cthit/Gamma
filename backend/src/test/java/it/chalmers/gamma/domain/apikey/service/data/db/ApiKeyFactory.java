package it.chalmers.gamma.domain.apikey.service.data.db;

import it.chalmers.gamma.domain.apikey.data.db.ApiKey;
import it.chalmers.gamma.domain.apikey.data.dto.ApiKeyDTO;

public class ApiKeyFactory {

    private ApiKeyFactory() { }

    public static ApiKey create(ApiKeyDTO apiKey) {
        return new ApiKey(apiKey);
    }

}
