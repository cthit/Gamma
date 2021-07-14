package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.app.apikey.ApiKeyRepository;
import it.chalmers.gamma.app.domain.ApiKey;
import it.chalmers.gamma.app.domain.ApiKeyId;
import it.chalmers.gamma.app.domain.ApiKeyToken;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ApiKeyJpaRepositoryAdapter implements ApiKeyRepository {

    private final ApiKeyJpaRepository repository;

    public ApiKeyJpaRepositoryAdapter(ApiKeyJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(ApiKey apiKey) {
        this.repository.save(new ApiKeyEntity(apiKey));
    }

    @Override
    public void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException {
        this.repository.deleteById(apiKeyId);
    }

    @Override
    public List<ApiKey> getAll() {
        return this.repository
                .findAll()
                .stream()
                .map(ApiKeyEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ApiKey> getById(ApiKeyId apiKeyId) {
        return this.repository.findById(apiKeyId).map(ApiKeyEntity::toDomain);
    }

    @Override
    public Optional<ApiKey> getByToken(ApiKeyToken apiKeyToken) {
        return this.repository.findByToken(apiKeyToken).map(ApiKeyEntity::toDomain);
    }

    @Override
    public ApiKeyToken generateNewToken(ApiKeyId apiKeyId) throws ApiKeyNotFoundException {
        ApiKeyToken token = ApiKeyToken.generate();
        ApiKeyEntity entity = this.repository.findById(apiKeyId).orElseThrow(ApiKeyNotFoundException::new);
        entity.setToken(token);
        this.repository.save(entity);
        return token;
    }

}
