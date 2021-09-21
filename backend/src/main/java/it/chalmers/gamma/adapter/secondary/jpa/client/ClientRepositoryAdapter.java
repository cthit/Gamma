package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.app.client.ClientRepository;
import it.chalmers.gamma.domain.client.Client;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ClientRepositoryAdapter implements ClientRepository {

    @Override
    public void create(Client client, ClientSecret clientSecret) {

    }

    @Override
    public void delete(ClientId clientId) throws ClientNotFoundException {

    }

    @Override
    public ClientSecret resetClientSecret(ClientId clientId) {
        return null;
    }

    @Override
    public List<Client> getAll() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Client> get(ClientId clientId) {
        return Optional.empty();
    }

    @Override
    public List<Client> getClientsByUserApproved(UserId id) {
        return Collections.emptyList();
    }
}
