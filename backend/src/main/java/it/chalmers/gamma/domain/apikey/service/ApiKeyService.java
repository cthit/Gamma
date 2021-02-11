package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.domain.apikey.data.ApiKey;
import it.chalmers.gamma.domain.apikey.data.ApiKeyDTO;
import it.chalmers.gamma.domain.apikey.data.ApiKeyRepository;
import it.chalmers.gamma.domain.apikey.exception.ApiKeyNotFoundException;
import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.util.TokenUtils;

import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyFinder apiKeyFinder;

    public ApiKeyService(ApiKeyRepository apiKeyRepository, ApiKeyFinder apiKeyFinder) {
        this.apiKeyRepository = apiKeyRepository;
        this.apiKeyFinder = apiKeyFinder;
    }

    public void createApiKey(ApiKeyDTO newApiKey) {
        this.apiKeyRepository.save(new ApiKey(newApiKey));
    }

    public void deleteApiKey(UUID id) throws ApiKeyNotFoundException {
        if(!this.apiKeyFinder.apiKeyExists(id)) {
            throw new ApiKeyNotFoundException();
        }

        this.apiKeyRepository.deleteById(id);
    }

}
