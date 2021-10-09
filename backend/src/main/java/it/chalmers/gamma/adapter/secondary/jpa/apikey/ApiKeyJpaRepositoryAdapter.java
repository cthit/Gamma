package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.app.port.repository.ApiKeyRepository;
import it.chalmers.gamma.app.domain.apikey.ApiKey;
import it.chalmers.gamma.app.domain.apikey.ApiKeyId;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ApiKeyJpaRepositoryAdapter implements ApiKeyRepository {

    private final ApiKeyJpaRepository repository;
    private final ApiKeyEntityConverter apiKeyEntityConverter;

    public ApiKeyJpaRepositoryAdapter(ApiKeyJpaRepository repository,
                                      ApiKeyEntityConverter apiKeyEntityConverter) {
        this.repository = repository;
        this.apiKeyEntityConverter = apiKeyEntityConverter;
    }

    @Override
    public void save(ApiKey apiKey) {
        this.repository.save(this.apiKeyEntityConverter.toEntity(apiKey));
    }

    @Override
    public void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException {
        this.repository.deleteById(apiKeyId.value());
    }

    @Override
    public List<ApiKey> getAll() {
        return this.repository
                .findAll()
                .stream()
                .map(this.apiKeyEntityConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ApiKey> getById(ApiKeyId apiKeyId) {
        return this.repository.findById(apiKeyId.value())
                .map(this.apiKeyEntityConverter::toDomain);
    }

    @Override
    public Optional<ApiKey> getByToken(ApiKeyToken apiKeyToken) {
        return this.repository.findByToken(apiKeyToken.value())
                .map(this.apiKeyEntityConverter::toDomain);
    }

}
