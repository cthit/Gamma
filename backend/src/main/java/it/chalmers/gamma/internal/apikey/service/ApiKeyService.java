package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.domain.ApiKeyId;
import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class ApiKeyService implements CreateEntity<ApiKeyDTO>, DeleteEntity<ApiKeyId> {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public void create(ApiKeyDTO newApiKey) {
        this.apiKeyRepository.save(new ApiKeyEntity(newApiKey));
    }

    @Override
    public void delete(ApiKeyId id) throws EntityNotFoundException {
        try{
            this.apiKeyRepository.deleteById(id);
        } catch(IllegalArgumentException e){
            throw new EntityNotFoundException();
        }
    }
}
