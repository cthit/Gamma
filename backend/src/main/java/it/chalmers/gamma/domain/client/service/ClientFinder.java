package it.chalmers.gamma.domain.client.service;

import it.chalmers.gamma.domain.client.data.Client;
import it.chalmers.gamma.domain.client.data.ClientRepository;
import it.chalmers.gamma.domain.client.data.ClientDTO;
import it.chalmers.gamma.domain.client.exception.ClientNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ClientFinder {

    private final ClientRepository clientRepository;

    public ClientFinder(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public boolean clientExists(String clientId) {
        return this.clientRepository.existsById(clientId);
    }

    public List<ClientDTO> getClients() {
        return this.clientRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ClientDTO getClient(String clientId) throws ClientNotFoundException {
        return toDTO(getClientEntity(clientId));
    }

    protected Client getClientEntity(ClientDTO client) throws ClientNotFoundException {
        return this.getClientEntity(client.getClientId());
    }

    public Client getClientEntity(String clientId) throws ClientNotFoundException {
        return this.clientRepository.findById(clientId)
                .orElseThrow(ClientNotFoundException::new);
    }

    protected ClientDTO toDTO(Client client) {
        return new ClientDTO.ClientDTOBuilder()
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .webServerRedirectUri(client.getWebServerRedirectUri())
                .accessTokenValidity(client.getAccessTokenValidity())
                .refreshTokenValidity(client.getRefreshTokenValidity())
                .autoApprove(client.isAutoApprove())
                .name(client.getName())
                .description(client.getDescription())
                .build();
    }
}
