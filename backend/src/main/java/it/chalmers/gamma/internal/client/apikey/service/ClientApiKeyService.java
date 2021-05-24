package it.chalmers.gamma.internal.client.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import org.springframework.stereotype.Service;

@Service
public class ClientApiKeyService implements CreateEntity<ClientApiKeyDTO> {

    private final ClientApiKeyRepository clientApiKeyRepository;

    public ClientApiKeyService(ClientApiKeyRepository clientApiKeyRepository) {
        this.clientApiKeyRepository = clientApiKeyRepository;
    }

    @Override
    public void create(ClientApiKeyDTO newClientApiKey) {
        this.clientApiKeyRepository.save(
                new ClientApiKeyEntity(
                        newClientApiKey.clientId(),
                        newClientApiKey.apiKeyId()
                )
        );
    }
}
