package it.chalmers.gamma.app.apikey;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import it.chalmers.gamma.app.apikey.domain.ApiKeyType;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;

@Component
public class ApiKeyFacade extends Facade {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFacade(AccessGuard accessGuard, ApiKeyRepository apiKeyRepository) {
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
        this.accessGuard.require(isAdmin());

        ApiKeyToken apiKeyToken = ApiKeyToken.generate();
        apiKeyRepository.create(
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
        this.accessGuard.require(isAdmin());

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

    public Optional<ApiKeyDTO> getById(UUID apiKeyId) {
        this.accessGuard.require(isAdmin());

        return this.apiKeyRepository.getById(new ApiKeyId(apiKeyId)).map(ApiKeyDTO::new);
    }

    public List<ApiKeyDTO> getAll() {
        this.accessGuard.require(isAdmin());

        return this.apiKeyRepository.getAll()
                .stream()
                .map(ApiKeyDTO::new)
                .toList();
    }

    public String resetApiKeyToken(UUID apiKeyId) throws ApiKeyRepository.ApiKeyNotFoundException {
        this.accessGuard.require(isAdmin());

        ApiKeyToken token = ApiKeyToken.generate();
        ApiKey apiKey = this.apiKeyRepository.getById(new ApiKeyId(apiKeyId)).orElseThrow();
        this.apiKeyRepository.create(apiKey.withApiKeyToken(token));
        return token.value();
    }

}
