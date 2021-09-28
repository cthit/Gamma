package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
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

    private final ClientJpaRepository clientJpaRepository;
    private final ClientApiKeyJpaRepository clientApiKeyJpaRepository;
    private final UserApprovalJpaRepository userApprovalJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ClientEntityConverter clientEntityConverter;

    public ClientRepositoryAdapter(ClientJpaRepository clientJpaRepository,
                                   ClientApiKeyJpaRepository clientApiKeyJpaRepository,
                                   UserApprovalJpaRepository userApprovalJpaRepository,
                                   UserJpaRepository userJpaRepository,
                                   ClientEntityConverter clientEntityConverter) {
        this.clientJpaRepository = clientJpaRepository;
        this.clientApiKeyJpaRepository = clientApiKeyJpaRepository;
        this.userApprovalJpaRepository = userApprovalJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.clientEntityConverter = clientEntityConverter;
    }

    @Override
    public void create(Client client) {
        this.clientJpaRepository.save(this.clientEntityConverter.toEntity(client));
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
        return this.clientJpaRepository.findAll().stream().map(ClientEntity::toDomain).toList();
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
