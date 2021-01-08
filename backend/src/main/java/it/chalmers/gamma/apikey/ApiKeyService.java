package it.chalmers.gamma.apikey;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.access.ApiKeyDTO;
import it.chalmers.gamma.apikey.response.ApiKeyDoesNotExistResponse;
import it.chalmers.gamma.util.TokenUtils;

import it.chalmers.gamma.util.UUIDUtil;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
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

    public ApiKeyDTO createApiKey(ApiKeyDTO request) {
        ApiKey apiKey = new ApiKey();
        Text description = new Text();
        description.setEn(request.getDescription().getEn());
        description.setSv(request.getDescription().getSv());
        apiKey.setName(request.getName());
        apiKey.setDescription(description);
        String key = TokenUtils.generateToken(50, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS);
        apiKey.setKey(key);
        return this.apiKeyRepository.save(apiKey).toDTO();
    }

    public void addApiKey(String clientName, String clientApiKey, Text description) {
        ApiKey apiKey = new ApiKey(clientName, clientApiKey, description);
        this.apiKeyRepository.save(apiKey);
    }

    public ApiKeyDTO getApiKeyDetails(String name) {
        if (UUIDUtil.validUUID(name)) {
            return this.apiKeyRepository.findById(UUID.fromString(name))
                    .orElseThrow(ApiKeyDoesNotExistResponse::new).toDTO();
        }
        return this.apiKeyRepository.findByName(name)
                        .orElseThrow(ApiKeyDoesNotExistResponse::new).toDTO();
    }

    public void deleteApiKey(UUID id) {
        this.apiKeyRepository.deleteById(id);
    }

    public List<ApiKeyDTO> getAllApiKeys() {
        return this.apiKeyRepository.findAll().stream().map(ApiKey::toDTO).collect(Collectors.toList());
    }

    public boolean apiKeyExists(UUID id) {
        return this.apiKeyRepository.existsById(id);
    }
}
