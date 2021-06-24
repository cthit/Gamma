package it.chalmers.gamma.app.apikey.service;

import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;

import it.chalmers.gamma.app.domain.ApiKeyToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repository;

    public ApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public void create(ApiKey newApiKey, ApiKeyToken apiKeyToken) {
        this.repository.save(new ApiKeyEntity(newApiKey, apiKeyToken));
    }

    public void delete(ApiKeyId id) throws ApiKeyNotFoundException {
        try{
            this.repository.deleteById(id);
        } catch(IllegalArgumentException e){
            throw new ApiKeyNotFoundException();
        }
    }

    public List<ApiKey> getAll() {
        return this.repository
                .findAll()
                .stream()
                .map(ApiKeyEntity::toDTO)
                .collect(Collectors.toList());
    }

    public ApiKey get(ApiKeyId id) throws ApiKeyNotFoundException {
        return this.repository.findById(id)
                        .orElseThrow(ApiKeyNotFoundException::new)
                        .toDTO();
    }

    public boolean isValidApiKey(ApiKeyToken apiKey) {
        return this.repository.existsByToken(apiKey);
    }

    public void resetApiKeyToken(ApiKeyId id, ApiKeyToken token) throws ApiKeyNotFoundException {
        ApiKeyEntity apiKey = this.repository.findById(id)
                .orElseThrow(ApiKeyNotFoundException::new);
        apiKey.setToken(token);
        this.repository.save(apiKey);
    }

    public static class ApiKeyNotFoundException extends Exception { }

}
