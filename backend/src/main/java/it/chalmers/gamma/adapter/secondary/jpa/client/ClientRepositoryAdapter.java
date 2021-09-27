package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.app.port.repository.ClientRepository;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.domain.client.ClientSecret;
import it.chalmers.gamma.app.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository repository;
    private final ClientApiKeyJpaRepository clientApiKeyJpaRepository;

    public ClientRepositoryAdapter(ClientJpaRepository repository,
                                   ClientApiKeyJpaRepository clientApiKeyJpaRepository) {
        this.repository = repository;
        this.clientApiKeyJpaRepository = clientApiKeyJpaRepository;
    }

    @Override
    public void create(Client client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ClientId clientId) throws ClientNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientSecret resetClientSecret(ClientId clientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Client> getAll() {
        return this.repository.findAll().stream().map(ClientEntity::toDomain).toList();
    }

    @Override
    public Optional<Client> get(ClientId clientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Client> getClientsByUserApproved(UserId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Client> getByApiKey(ApiKeyToken apiKeyToken) {
        return this.clientApiKeyJpaRepository
                .findByApiKey_Token(apiKeyToken.value())
                .map(ClientEntity::toDomain);
    }
}
