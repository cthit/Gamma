package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ApiKey;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.ApiKeyRepository;
import it.chalmers.gamma.requests.CreateApiKeyRequest;
import it.chalmers.gamma.util.TokenUtils;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }
    public boolean isValidApiKey(String apiKey) {
        return this.apiKeyRepository.existsByKey(apiKey);
    }

    public String createApiKey(CreateApiKeyRequest request) {
        ApiKey apiKey = new ApiKey();
        Text description = new Text();
        description.setEn(request.getDescription().getEn());
        description.setSv(request.getDescription().getSv());
        apiKey.setName(request.getName());
        apiKey.setDescription(description);
        String key = TokenUtils.generateToken(50, TokenUtils.CharacterTypes.allValues());
        apiKey.setKey(key);
        this.apiKeyRepository.save(apiKey);
        return key;
    }

    public ApiKey getApiKeyDetails(UUID id) {
        return this.apiKeyRepository.getById(id);
    }

    public ApiKey getApiKeyDetails(String name) {
        return this.apiKeyRepository.getByName(name);
    }

    public void deleteApiKey(UUID id) {
        this.apiKeyRepository.deleteById(id);
    }

    public List<ApiKey> getAllApiKeys() {
        return this.apiKeyRepository.findAll();
    }

    public boolean apiKeyExists(UUID id) {
        return this.apiKeyRepository.existsById(id);
    }
}
