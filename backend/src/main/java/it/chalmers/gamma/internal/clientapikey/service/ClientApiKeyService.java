package it.chalmers.gamma.internal.clientapikey.service;

import it.chalmers.gamma.domain.ClientApiKeyPair;
import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.internal.client.service.ClientService;
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
                .toDTO();
    }

}
