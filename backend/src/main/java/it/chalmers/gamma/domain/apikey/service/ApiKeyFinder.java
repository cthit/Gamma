package it.chalmers.gamma.domain.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.abstraction.GetAllEntities;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiKeyFinder implements GetEntity<ApiKeyId, ApiKeyInformationDTO>, GetAllEntities<ApiKeyInformationDTO> {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFinder(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public boolean isValidApiKey(ApiKeyToken apiKey) {
        return this.apiKeyRepository.existsByKey(apiKey);
    }

    public boolean apiKeyExists(ApiKeyId id) {
        return this.apiKeyRepository.existsById(id);
    }

    @Override
    public List<ApiKeyInformationDTO> getAll() {
        return this.apiKeyRepository
                .findAll()
                .stream()
                .map(ApiKey::toDTO)
                .map(ApiKeyInformationDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public ApiKeyInformationDTO get(ApiKeyId id) throws EntityNotFoundException {
        return new ApiKeyInformationDTO(getApiKeyEntity(id).toDTO());
    }

    protected ApiKey getApiKeyEntity(ApiKeyId id) throws EntityNotFoundException {
        return this.apiKeyRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

}
