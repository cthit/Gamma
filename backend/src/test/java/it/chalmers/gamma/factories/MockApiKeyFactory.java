package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.dto.access.ApiKeyDTO;
import it.chalmers.gamma.requests.CreateApiKeyRequest;
import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockApiKeyFactory {

    @Autowired
    private ApiKeyService apiKeyService;

    public ApiKeyDTO generateApiKey() {
        return new ApiKeyDTO(
                UUID.randomUUID(),
                GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE),
                GenerationUtils.generateText(),
                Instant.now(),
                Instant.now(),
                GenerationUtils.generateRandomString(20, CharacterTypes.LOWERCASE)
        );
    }

    public ApiKeyDTO saveApiKey(ApiKeyDTO apiKeyDTO) {
        return this.apiKeyService.createApiKey(apiKeyDTO);
    }

    public CreateApiKeyRequest createValidRequest(ApiKeyDTO apiKey) {
        CreateApiKeyRequest request = new CreateApiKeyRequest();
        request.setName(apiKey.getName());
        request.setDescription(apiKey.getDescription());
        return request;
    }

}
