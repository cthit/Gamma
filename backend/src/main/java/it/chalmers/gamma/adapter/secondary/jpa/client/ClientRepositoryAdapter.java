package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalJpaRepository;
import it.chalmers.gamma.app.domain.client.ClientUid;
import it.chalmers.gamma.app.repository.ClientRepository;
import it.chalmers.gamma.app.domain.apikey.ApiKeyToken;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.domain.user.UserId;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository clientJpaRepository;
    private final ClientApiKeyJpaRepository clientApiKeyJpaRepository;
    private final ClientEntityConverter clientEntityConverter;
    private final UserApprovalJpaRepository userApprovalJpaRepository;

    public ClientRepositoryAdapter(ClientJpaRepository clientJpaRepository,
                                   ClientApiKeyJpaRepository clientApiKeyJpaRepository,
                                   ClientEntityConverter clientEntityConverter,
                                   UserApprovalJpaRepository userApprovalJpaRepository) {
        this.clientJpaRepository = clientJpaRepository;
        this.clientApiKeyJpaRepository = clientApiKeyJpaRepository;
        this.clientEntityConverter = clientEntityConverter;
        this.userApprovalJpaRepository = userApprovalJpaRepository;
    }

    @Override
    public void save(Client client) {
        this.clientJpaRepository.save(this.clientEntityConverter.toEntity(client));
    }

    @Override
    public void delete(ClientUid clientUid) throws ClientNotFoundException {
        this.clientJpaRepository.deleteById(clientUid.value());
    }

    @Override
    public List<Client> getAll() {
        return this.clientJpaRepository.findAll()
                .stream()
                .map(clientEntityConverter::toDomain)
                .toList();
    }

    @Override
    public Optional<Client> get(ClientUid clientUid) {
        return this.clientJpaRepository.findById(clientUid.value())
                .map(this.clientEntityConverter::toDomain);
    }

    @Override
    public Optional<Client> get(ClientId clientId) {
        return this.clientJpaRepository.findByClientId(clientId.value())
                .map(this.clientEntityConverter::toDomain);
    }

    @Override
    public List<Client> getClientsByUserApproved(UserId id) {
        return this.userApprovalJpaRepository.findAllById_User_Id(id.value())
                .stream()
                .map(this.clientEntityConverter::toDomain)
                .toList();
    }

    @Override
    public Optional<Client> getByApiKey(ApiKeyToken apiKeyToken) {
        return this.clientApiKeyJpaRepository
                .findByApiKey_Token(apiKeyToken.value())
                .map(ClientApiKeyEntity::getClient)
                .map(this.clientEntityConverter::toDomain);
    }
}
