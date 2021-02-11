package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.domain.apikey.data.ApiKey;
import it.chalmers.gamma.domain.apikey.data.ApiKeyRepository;
import it.chalmers.gamma.domain.apikey.data.ApiKeyDTO;
import it.chalmers.gamma.domain.apikey.exception.ApiKeyNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApiKeyFinder {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFinder(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public List<ApiKeyDTO> getApiKeys() {
        return this.apiKeyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public boolean isValidApiKey(String apiKey) {
        return this.apiKeyRepository.existsByKey(apiKey);
    }

    public boolean apiKeyExists(UUID id) {
        return this.apiKeyRepository.existsById(id);
    }

    public ApiKeyDTO getApiKey(UUID id) throws ApiKeyNotFoundException {
        return toDTO(getApiKeyEntity(id));
    }

    protected ApiKey getApiKeyEntity(UUID id) throws ApiKeyNotFoundException {
        return this.apiKeyRepository.findById(id)
                .orElseThrow(ApiKeyNotFoundException::new);
    }

    protected ApiKeyDTO toDTO(ApiKey apiKey) {
        return new ApiKeyDTO(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getDescription(),
                apiKey.getKey()
        );
    }

}
