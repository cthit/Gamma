package it.chalmers.gamma.app.client;

import it.chalmers.gamma.adapter.secondary.jpa.client.ClientApiKeyEntity;
import it.chalmers.gamma.adapter.secondary.jpa.client.ClientApiKeyRepository;
import it.chalmers.gamma.app.domain.ClientApiKeyPair;
import it.chalmers.gamma.app.domain.ClientId;
import org.springframework.stereotype.Service;

@Service
public class ClientApiKeyService {

    private final ClientApiKeyRepository clientApiKeyRepository;

    public ClientApiKeyService(ClientApiKeyRepository clientApiKeyRepository) {
        this.clientApiKeyRepository = clientApiKeyRepository;
    }

    public void create(ClientApiKeyPair newClientApiKey) {
        this.clientApiKeyRepository.save(
                new ClientApiKeyEntity(
                        newClientApiKey.clientId(),
                        newClientApiKey.apiKeyId()
                )
        );
    }

    public ClientApiKeyPair get(ClientId id) throws ClientService.ClientNotFoundException {
        return this.clientApiKeyRepository.findById(id)
                .orElseThrow(ClientService.ClientNotFoundException::new)
                .toDomain();
    }

}
