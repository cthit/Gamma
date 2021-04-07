package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.apikey.domain.ApiKeyId;
import it.chalmers.gamma.domain.apikey.data.db.ApiKey;
import it.chalmers.gamma.domain.apikey.data.dto.ApiKeyDTO;
import it.chalmers.gamma.domain.apikey.data.db.ApiKeyRepository;

import org.springframework.stereotype.Service;

@Service
public class ApiKeyService implements CreateEntity<ApiKeyDTO>, DeleteEntity<ApiKeyId> {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public void create(ApiKeyDTO newApiKey) {
        this.apiKeyRepository.save(new ApiKey(newApiKey));
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
