package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ApiKeyRepositoryAdapter implements ApiKeyRepository {

    private final ApiKeyJpaRepository repository;
    private final ApiKeyEntityConverter apiKeyEntityConverter;

    public ApiKeyRepositoryAdapter(ApiKeyJpaRepository repository,
                                   ApiKeyEntityConverter apiKeyEntityConverter) {
        this.repository = repository;
        this.apiKeyEntityConverter = apiKeyEntityConverter;
    }

    @Override
    public void create(ApiKey apiKey) throws ApiKeyAlreadyExistRuntimeException {
        try {
            this.repository.save(this.apiKeyEntityConverter.toEntity(apiKey));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof EntityExistsException) {
                throw new ApiKeyAlreadyExistRuntimeException();
            }
            throw e;
        }
    }

    @Override
    public void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException {
        try {
            this.repository.deleteById(apiKeyId.value());
        } catch (EmptyResultDataAccessException e) {
            throw new ApiKeyNotFoundException();
        }
    }

    @Override
    public ApiKeyToken resetApiKeyToken(ApiKeyId apiKeyId) throws ApiKeyNotFoundException {
        ApiKeyToken newToken = ApiKeyToken.generate();
        ApiKeyEntity entity;
        try {
            entity = this.repository.getById(apiKeyId.value());
            entity.setApiKeyToken(newToken.value());
            this.repository.save(entity);
            return newToken;
        } catch (EntityNotFoundException e) {
            throw new ApiKeyNotFoundException();
        }
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
