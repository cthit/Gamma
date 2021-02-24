package it.chalmers.gamma.domain.client.service;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.GetAllEntities;
import it.chalmers.gamma.domain.GetEntity;
import it.chalmers.gamma.domain.client.domain.ClientId;
import it.chalmers.gamma.domain.client.data.db.Client;
import it.chalmers.gamma.domain.client.data.dto.ClientDTO;
import it.chalmers.gamma.domain.client.data.db.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientFinder implements GetEntity<ClientId, ClientDTO>, GetAllEntities<ClientDTO> {

    private final ClientRepository clientRepository;

    public ClientFinder(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public boolean clientExists(ClientId clientId) {
        return this.clientRepository.existsById(clientId);
    }

    public List<ClientDTO> getAll() {
        return this.clientRepository.findAll().stream().map(Client::toDTO).collect(Collectors.toList());
    }

    public ClientDTO get(ClientId clientId) throws EntityNotFoundException {
        return this.clientRepository.findById(clientId)
                .orElseThrow(EntityNotFoundException::new)
                .toDTO();
    }
}
