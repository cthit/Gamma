package it.chalmers.gamma.internal.apikey.service;

import it.chalmers.gamma.domain.ApiKeyId;

import it.chalmers.gamma.domain.ApiKeyToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public void create(ApiKeyDTO newApiKey) {
        this.apiKeyRepository.save(new ApiKeyEntity(newApiKey));
    }

    public void delete(ApiKeyId id) throws ApiKeyNotFoundException {
        try{
            this.apiKeyRepository.deleteById(id);
        } catch(IllegalArgumentException e){
            throw new ApiKeyNotFoundException();
        }
    }

    public List<ApiKeyInformationDTO> getAll() {
        return this.apiKeyRepository
                .findAll()
                .stream()
                .map(ApiKeyEntity::toDTO)
                .map(ApiKeyInformationDTO::new)
                .collect(Collectors.toList());
    }

    public ApiKeyInformationDTO get(ApiKeyId id) throws ApiKeyNotFoundException {
        return new ApiKeyInformationDTO(
                this.apiKeyRepository.findById(id)
                        .orElseThrow(ApiKeyNotFoundException::new)
                        .toDTO()
        );
    }

    public boolean isValidApiKey(ApiKeyToken apiKey) {
        return this.apiKeyRepository.existsByKey(apiKey);
    }

    public static class ApiKeyNotFoundException extends Exception { }

}
