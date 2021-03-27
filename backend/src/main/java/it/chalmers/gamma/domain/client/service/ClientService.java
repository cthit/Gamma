package it.chalmers.gamma.domain.client.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.client.domain.ClientId;
import it.chalmers.gamma.domain.client.data.db.Client;
import it.chalmers.gamma.domain.client.data.dto.ClientDTO;
import it.chalmers.gamma.domain.client.data.db.ClientRepository;
import it.chalmers.gamma.domain.client.data.dto.ClientDetailsDTO;
import it.chalmers.gamma.domain.client.controller.response.ClientNotFoundResponse;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements ClientDetailsService, CreateEntity<ClientDTO>, DeleteEntity<ClientId> {


    private final ClientRepository clientRepository;
    private final ClientFinder clientFinder;

    public ClientService(ClientRepository clientRepository, ClientFinder clientFinder) {
        this.clientRepository = clientRepository;
        this.clientFinder = clientFinder;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return this.clientRepository.findById(new ClientId(clientId))
                .map(Client::toDTO)
                .map(ClientDetailsDTO::new)
                .orElseThrow(ClientNotFoundResponse::new);
    }

    public void create(ClientDTO newClient) {
        this.clientRepository.save(new Client(newClient));
    }

    public void delete(ClientId clientId) throws EntityNotFoundException {
        if(!this.clientFinder.clientExists(clientId)) {
            throw new EntityNotFoundException();
        }

        this.clientRepository.deleteById(clientId);
    }

}
