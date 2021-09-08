package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.domain.apikey.ApiKey;
import it.chalmers.gamma.domain.apikey.ApiKeyId;
import it.chalmers.gamma.domain.apikey.ApiKeyToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ApiKeyFacade extends Facade {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFacade(AccessGuard accessGuard, ApiKeyRepository apiKeyRepository) {
        super(accessGuard);
        this.apiKeyRepository = apiKeyRepository;
    }

    public ApiKeyToken create(ApiKey apiKey) {
        accessGuard.requireIsAdmin();
        ApiKeyToken apiKeyToken = ApiKeyToken.generate();
//        apiKeyRepository.create(apiKey, apiKeyToken);
        return apiKeyToken;
    }

    public void delete(ApiKeyId apiKeyId) throws ApiKeyRepository.ApiKeyNotFoundException {
        accessGuard.requireIsAdmin();
        apiKeyRepository.delete(apiKeyId);
    }

    public Optional<ApiKey> get(ApiKeyId apiKeyId) {
        return this.apiKeyRepository.getById(apiKeyId);
    }

    public List<ApiKey> getAll() {
        accessGuard.requireIsAdmin();
        return this.apiKeyRepository.getAll();
    }

    public ApiKeyToken resetApiKeyToken(ApiKeyId apiKeyId) throws ApiKeyRepository.ApiKeyNotFoundException {
        accessGuard.requireIsAdmin();
        return null;
//        return this.apiKeyRepository.resetApiKeyToken(apiKeyId);
    }

}
