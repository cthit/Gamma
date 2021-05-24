package it.chalmers.gamma.internal.client.apikey.service;

import it.chalmers.gamma.domain.ClientId;
import it.chalmers.gamma.util.domain.abstraction.GetEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClientApiKeyFinder implements GetEntity<ClientId, ClientApiKeyDTO> {

    private final ClientApiKeyRepository clientApiKeyRepository;

    public ClientApiKeyFinder(ClientApiKeyRepository clientApiKeyRepository) {
        this.clientApiKeyRepository = clientApiKeyRepository;
    }

    @Override
    public ClientApiKeyDTO get(ClientId id) throws EntityNotFoundException {
        return this.clientApiKeyRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new)
                .toDTO();
    }
}
