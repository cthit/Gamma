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

    public boolean clientExists(UUID id) {
        return this.clientRepository.existsById(id);
    }

    public boolean clientExistsByClientId(String clientId) {
        return this.clientRepository.existsByClientId(clientId);
    }

    public List<ClientDTO> getClients() {
        return this.clientRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ClientDTO getClient(UUID id) throws ClientNotFoundException {
        return this.toDTO(getClientEntity(id));
    }

    public ClientDTO getClient(String clientId) throws ClientNotFoundException {
        return toDTO(getClientEntity(clientId));
    }

    protected Client getClientEntity(UUID id) throws ClientNotFoundException {
        return this.clientRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);
    }

    protected Client getClientEntity(ClientDTO client) throws ClientNotFoundException {
        return this.getClientEntity(client.getClientId());
    }

    public Client getClientEntity(String clientId) throws ClientNotFoundException {
        return this.clientRepository.findByClientId(clientId)
                .orElseThrow(ClientNotFoundException::new);
    }

    protected ClientDTO toDTO(Client client) {
        return new ClientDTO.ClientDTOBuilder()
                .id(client.getId())
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
