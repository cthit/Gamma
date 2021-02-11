package it.chalmers.gamma.domain.client.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.client.data.Client;
import it.chalmers.gamma.domain.client.data.ClientRepository;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import it.chalmers.gamma.domain.client.exception.ClientNotFoundException;
import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.domain.client.controller.response.ClientDoesNotExistResponse;
import it.chalmers.gamma.util.TokenUtils;

import it.chalmers.gamma.util.UUIDUtil;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements ClientDetailsService {

    @Value("${application.auth.accessTokenValidityTime}")
    private int accessTokenValidityTime;

    @Value("${application.auth.refreshTokenValidityTime}")
    private int refreshTokenValidityTime;

    private final ClientRepository clientRepository;
    private final ClientFinder clientFinder;

    public ClientService(ClientRepository clientRepository, ClientFinder clientFinder) {
        this.clientRepository = clientRepository;
        this.clientFinder = clientFinder;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return this.clientRepository.findByClientId(clientId)
                .map(this.clientFinder::toDTO)
                .orElseThrow(ClientDoesNotExistResponse::new);
    }

    public void createClient(ClientDTO newClient) {
        this.clientRepository.save(new Client(newClient));
    }

    public void removeClient(UUID id) throws ClientNotFoundException {
        if(!this.clientFinder.clientExists(id)) {
            throw new ClientNotFoundException();
        }

        this.clientRepository.deleteById(id);
    }

    public void editClient(ClientDTO newEdit) throws IDsNotMatchingException, ClientNotFoundException {
        Client client = this.clientFinder.getClientEntity(newEdit);
        client.apply(newEdit);
        this.clientRepository.save(client);
    }

}
