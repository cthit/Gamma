package it.chalmers.gamma.internal.client.apikey.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
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
                new ClientApiKey(
                        newClientApiKey.clientId(),
                        newClientApiKey.apiKeyId()
                )
        );
    }
}
