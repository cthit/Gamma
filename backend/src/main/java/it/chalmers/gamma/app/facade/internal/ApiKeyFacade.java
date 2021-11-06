package it.chalmers.gamma.app.facade.internal;

import it.chalmers.gamma.app.facade.Facade;
import it.chalmers.gamma.app.usecase.AccessGuardUseCase;
import it.chalmers.gamma.app.repository.ApiKeyRepository;
import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.apikey.ApiKeyId;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.app.domain.apikey.ApiKeyType;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.common.Text;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ApiKeyFacade extends Facade {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFacade(AccessGuardUseCase accessGuard, ApiKeyRepository apiKeyRepository) {
        super(accessGuard);
        this.apiKeyRepository = apiKeyRepository;
    }

    public String[] getApiKeyTypes() {
        ApiKeyType[] types = ApiKeyType.values();
        String[] s = new String[types.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = types[i].name();
        }
        return s;
    }

    public record NewApiKey(
            String prettyName,
            String svDescription,
            String enDescription,
            String keyType) {
    }

    public String create(NewApiKey newApiKey) {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        ApiKeyToken apiKeyToken = ApiKeyToken.generate();
        apiKeyRepository.save(
                new ApiKey(
                        ApiKeyId.generate(),
                        new PrettyName(newApiKey.prettyName),
                        new Text(newApiKey.svDescription, newApiKey.enDescription),
                        ApiKeyType.valueOf(newApiKey.keyType),
                        apiKeyToken
                )
        );
        return apiKeyToken.value();
    }

    public void delete(UUID apiKeyId) throws ApiKeyRepository.ApiKeyNotFoundException {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        apiKeyRepository.delete(new ApiKeyId(apiKeyId));
    }

    public record ApiKeyDTO(UUID id,
                            String prettyName,
                            String svDescription,
                            String enDescription,
                            String keyType) {
        public ApiKeyDTO(ApiKey apiKey) {
            this(apiKey.id().value(),
                    apiKey.prettyName().value(),
                    apiKey.description().sv().value(),
                    apiKey.description().en().value(),
                    apiKey.keyType().name()
            );
        }
    }

    public Optional<ApiKeyDTO> getByToken(String token) {
        return this.apiKeyRepository.getByToken(new ApiKeyToken(token)).map(ApiKeyDTO::new);
    }

    public Optional<ApiKeyDTO> getById(String apiKeyId) {
        return this.apiKeyRepository.getById(new ApiKeyId(apiKeyId)).map(ApiKeyDTO::new);
    }

    public List<ApiKeyDTO> getAll() {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        return this.apiKeyRepository.getAll()
                .stream()
                .map(ApiKeyDTO::new)
                .toList();
    }

    public String resetApiKeyToken(UUID apiKeyId) throws ApiKeyRepository.ApiKeyNotFoundException {
        this.accessGuard.require()
                .isAdmin()
                .ifNotThrow();

        ApiKeyToken token = ApiKeyToken.generate();
        ApiKey apiKey = this.apiKeyRepository.getById(new ApiKeyId(apiKeyId)).orElseThrow();
        this.apiKeyRepository.save(apiKey.withApiKeyToken(token));
        return token.value();
    }

}
