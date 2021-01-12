package it.chalmers.gamma.client;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ITClientFinder {

    private final ITClientRepository clientRepository;

    public ITClientFinder(ITClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<ITClientDTO> getClient(UUID id) {
        return this.clientRepository.findById(id).map(ITClient::toDTO);
    }

    protected ITClient getClient(ITClientDTO clientDTO) {
        return this.clientRepository.findById(clientDTO.getId()).orElse(null);
    }

    public Optional<ITClientDTO> getClient(String clientId) {
        return this.clientRepository.findByClientId(clientId).map(ITClient::toDTO);
    }

}
