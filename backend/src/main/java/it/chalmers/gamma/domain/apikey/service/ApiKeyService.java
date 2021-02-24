package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.domain.CreateEntity;
import it.chalmers.gamma.domain.DeleteEntity;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.apikey.domain.ApiKeyId;
import it.chalmers.gamma.domain.apikey.data.db.ApiKey;
import it.chalmers.gamma.domain.apikey.data.dto.ApiKeyDTO;
import it.chalmers.gamma.domain.apikey.data.db.ApiKeyRepository;

import org.springframework.stereotype.Service;

@Service
public class ApiKeyService implements CreateEntity<ApiKeyDTO>, DeleteEntity<ApiKeyId> {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyFinder apiKeyFinder;

    public ApiKeyService(ApiKeyRepository apiKeyRepository, ApiKeyFinder apiKeyFinder) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyFinder = apiKeyFinder;
    }

    @Override
    public void create(ApiKeyDTO newApiKey) {
        this.apiKeyRepository.save(new ApiKey(newApiKey));
    }

    @Override
    public void delete(ApiKeyId id) throws EntityNotFoundException {
        if(!this.apiKeyFinder.apiKeyExists(id)) {
            throw new EntityNotFoundException();
        }

        this.apiKeyRepository.deleteById(id);
    }
}
