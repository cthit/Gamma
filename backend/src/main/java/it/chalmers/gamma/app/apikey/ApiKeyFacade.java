package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;

import java.util.List;

public class ApiKeyFacade extends Facade {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFacade(AccessGuard accessGuard, ApiKeyRepository apiKeyRepository) {
        super(accessGuard);
        this.apiKeyRepository = apiKeyRepository;
    }

    public void create(ApiKey apiKey) {
        accessGuard.requireIsAdmin();
        apiKeyRepository.create(apiKey);
    }

    public void delete(ApiKeyId apiKeyId) throws ApiKeyRepository.ApiKeyNotFoundException {
        accessGuard.requireIsAdmin();
        apiKeyRepository.delete(apiKeyId);
    }

    public List<ApiKey> getAll() {
        accessGuard.requireIsAdmin();
        return this.apiKeyRepository.getAll();
    }

}
