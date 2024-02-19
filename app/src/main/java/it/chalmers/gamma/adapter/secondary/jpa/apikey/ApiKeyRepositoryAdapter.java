package it.chalmers.gamma.adapter.secondary.jpa.apikey;

import it.chalmers.gamma.adapter.secondary.jpa.client.apikey.ClientApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.apikey.ClientApiKeyJpaRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKey;
import it.chalmers.gamma.app.apikey.domain.ApiKeyId;
import it.chalmers.gamma.app.apikey.domain.ApiKeyRepository;
import it.chalmers.gamma.app.apikey.domain.ApiKeyToken;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ApiKeyRepositoryAdapter implements ApiKeyRepository {

    private final ApiKeyJpaRepository repository;
    private final ApiKeyEntityConverter apiKeyEntityConverter;
    private final ClientApiKeyJpaRepository clientApiKeyJpaRepository;

    public ApiKeyRepositoryAdapter(ApiKeyJpaRepository repository,
                                   ApiKeyEntityConverter apiKeyEntityConverter,
                                   ClientApiKeyJpaRepository clientApiKeyJpaRepository) {
        this.repository = repository;
        this.apiKeyEntityConverter = apiKeyEntityConverter;
        this.clientApiKeyJpaRepository = clientApiKeyJpaRepository;
    }

    @Override
    public void create(ApiKey apiKey) throws ApiKeyAlreadyExistRuntimeException {
        try {
            this.repository.saveAndFlush(toEntity(apiKey));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof EntityExistsException) {
                throw new ApiKeyAlreadyExistRuntimeException();
            }
            throw e;
        }
    }

    @Transactional
    @Override
    public void delete(ApiKeyId apiKeyId) throws ApiKeyNotFoundException {
        try {

            //First check if this ApiKey is connected to a Client.
            ClientApiKeyEntity clientApiKeyEntity = this.clientApiKeyJpaRepository.getByApiKey_Id(apiKeyId.value());
            if (clientApiKeyEntity != null) {
                clientApiKeyEntity.removeApiKey();
                this.clientApiKeyJpaRepository.saveAndFlush(clientApiKeyEntity);
            } else {
                this.repository.deleteById(apiKeyId.value());
            }

        } catch (EmptyResultDataAccessException e) {
            throw new ApiKeyNotFoundException();
        }
    }

    @Override
    public ApiKeyToken resetApiKeyToken(ApiKeyId apiKeyId) throws ApiKeyNotFoundException {
        ApiKeyToken newToken = ApiKeyToken.generate();
        ApiKeyEntity entity = this.repository.findById(apiKeyId.value())
                .orElseThrow(ApiKeyNotFoundException::new);
        entity.setApiKeyToken(newToken.value());
        this.repository.saveAndFlush(entity);
        return newToken;
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

    private ApiKeyEntity toEntity(ApiKey apiKey) {
        ApiKeyEntity apiKeyEntity = new ApiKeyEntity();

        apiKeyEntity.id = apiKey.id().value();
        apiKeyEntity.token = apiKey.apiKeyToken().value();
        apiKeyEntity.prettyName = apiKey.prettyName().value();
        apiKeyEntity.keyType = apiKey.keyType();
        apiKeyEntity.description.apply(apiKey.description());

        return apiKeyEntity;
    }

}
