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

    public ApiKeyDTO createApiKey(ApiKeyDTO newApiKey) {
        ApiKey apiKey = new ApiKey();
        Text description = new Text();
        description.setEn(newApiKey.getDescription().getEn());
        description.setSv(newApiKey.getDescription().getSv());
        apiKey.setName(newApiKey.getName());
        apiKey.setDescription(description);
        String key = TokenUtils.generateToken(50, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS);
        apiKey.setKey(key);
        return this.apiKeyFinder.toDTO(this.apiKeyRepository.save(apiKey));
    }

    public void addApiKey(String clientName, String clientApiKey, Text description) {
        ApiKey apiKey = new ApiKey(clientName, clientApiKey, description);
        this.apiKeyRepository.save(apiKey);
    }

    public void deleteApiKey(UUID id) {
        this.apiKeyRepository.deleteById(id);
    }

}
