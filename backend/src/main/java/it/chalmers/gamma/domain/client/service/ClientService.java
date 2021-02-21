package it.chalmers.gamma.domain.client.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.data.Client;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import it.chalmers.gamma.domain.client.data.ClientRepository;
import it.chalmers.gamma.domain.client.data.ClientDetailsDTO;
import it.chalmers.gamma.domain.client.exception.ClientNotFoundException;
import it.chalmers.gamma.domain.client.controller.response.ClientDoesNotExistResponse;

import it.chalmers.gamma.domain.text.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements ClientDetailsService {


    private final ClientRepository clientRepository;
    private final ClientFinder clientFinder;

    public ClientService(ClientRepository clientRepository, ClientFinder clientFinder) {
        this.clientRepository = clientRepository;
        this.clientFinder = clientFinder;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return this.clientRepository.findById(new ClientId(clientId))
                .map(this.clientFinder::toDTO)
                .map(ClientDetailsDTO::new)
                .orElseThrow(ClientDoesNotExistResponse::new);
    }

    public void createClient(ClientDTO newClient) {
        this.clientRepository.save(new Client(newClient));
    }

    public void removeClient(ClientId clientId) throws ClientNotFoundException {
        if(!this.clientFinder.clientExists(clientId)) {
            throw new ClientNotFoundException();
        }

        this.clientRepository.deleteById(clientId);
    }

    public void editClient(ClientDTO newEdit) throws IDsNotMatchingException, ClientNotFoundException {
        Client client = this.clientFinder.getClientEntity(newEdit);
        client.apply(newEdit);
        this.clientRepository.save(client);
    }

    public void editClient(ClientId clientId, Text description) throws ClientNotFoundException {
        Client client = this.clientFinder.getClientEntity(clientId);
        client.getDescription().apply(description);
        this.clientRepository.save(client);
    }
}
