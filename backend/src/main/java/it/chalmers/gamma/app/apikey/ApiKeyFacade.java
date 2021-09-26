package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.domain.apikey.ApiKey;
import it.chalmers.gamma.domain.apikey.ApiKeyId;
import it.chalmers.gamma.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.domain.apikey.ApiKeyType;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.common.Text;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ApiKeyFacade extends Facade {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFacade(AccessGuard accessGuard, ApiKeyRepository apiKeyRepository) {
        super(accessGuard);
        this.apiKeyRepository = apiKeyRepository;
    }

    public String[] getApiKeyTypes() {
        return (String[]) Arrays.stream(ApiKeyType.values()).map(ApiKeyType::name).toArray();
    }

    public record NewApiKey(
            String prettyName,
            String svText,
            String enText,
            String keyType) {
    }

    public String create(NewApiKey newApiKey) {
        accessGuard.requireIsAdmin();
        ApiKeyToken apiKeyToken = ApiKeyToken.generate();
        apiKeyRepository.create(
                new ApiKey(
                        ApiKeyId.generate(),
                        new PrettyName(newApiKey.prettyName),
                        new Text(newApiKey.svText, newApiKey.enText),
                        ApiKeyType.valueOf(newApiKey.keyType),
                        apiKeyToken
                )
        );
        return apiKeyToken.value();
    }

    public void delete(UUID apiKeyId) throws ApiKeyRepository.ApiKeyNotFoundException {
        accessGuard.requireIsAdmin();
        apiKeyRepository.delete(new ApiKeyId(apiKeyId));
    }

    public record ApiKeyDTO(UUID id, String prettyName, String svText, String enText) {
        public ApiKeyDTO(ApiKey apiKey) {
            this(apiKey.id().value(), apiKey.prettyName().value(), apiKey.description().sv().value(), apiKey.description().en().value());
        }
    }

    public Optional<ApiKeyDTO> getByToken(String token) {
        return this.apiKeyRepository.getByToken(new ApiKeyToken(token)).map(ApiKeyDTO::new);
    }

    public Optional<ApiKeyDTO> getById(String apiKeyId) {
        return this.apiKeyRepository.getById(new ApiKeyId(apiKeyId)).map(ApiKeyDTO::new);
    }

    public List<ApiKeyDTO> getAll() {
        accessGuard.requireIsAdmin();
        return this.apiKeyRepository.getAll()
                .stream()
                .map(ApiKeyDTO::new)
                .toList();
    }

    public String resetApiKeyToken(UUID apiKeyId) throws ApiKeyRepository.ApiKeyNotFoundException {
        accessGuard.requireIsAdmin();
        ApiKeyToken token = ApiKeyToken.generate();
        ApiKey apiKey = this.apiKeyRepository.getById(new ApiKeyId(apiKeyId)).orElseThrow();
        this.apiKeyRepository.save(apiKey.withApiKeyToken(token));
        return token.value();
    }

}
