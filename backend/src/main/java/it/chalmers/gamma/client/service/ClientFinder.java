package it.chalmers.gamma.client.service;

import it.chalmers.gamma.client.data.Client;
import it.chalmers.gamma.client.data.ClientRepository;
import it.chalmers.gamma.client.dto.ClientDTO;
import it.chalmers.gamma.client.exception.ClientNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ClientFinder {

    private final ClientRepository clientRepository;

    public ClientFinder(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<ClientDTO> getClient(UUID id) {
        return this.clientRepository.findById(id).map(Client::toDTO);
    }

    protected Client getClientEntity(ClientDTO client) throws ClientNotFoundException {
        return this.clientRepository.findByClientId(client.getClientId())
                .orElseThrow(ClientNotFoundException::new);
    }


    public Client getClientEntity(String clientId) throws ClientNotFoundException {
        return this.clientRepository.findByClientId(clientId)
                .orElseThrow(ClientNotFoundException::new);
    }

    public ClientDTO getClient(String clientId) throws ClientNotFoundException {
        return toDTO(getClientEntity(clientId));
    }

    protected ClientDTO toDTO(Client client) {
        return new ClientDTO.ClientDTOBuilder()
                .setId(client.getId())
                .setClientId(client.getClientId())
                .setClientSecret(client.getClientSecret())
                .setWebServerRedirectUri(client.getWebServerRedirectUri())
                .setAccessTokenValidity(client.getAccessTokenValidity())
                .setRefreshTokenValidity(client.getRefreshTokenValidity())
                .setAutoApprove(client.isAutoApprove())
                .setName(client.getName())
                .setDescription(client.getDescription())
                .setCreatedAt(client.getCreatedAt())
                .setLastModifiedAt(client.getLastModifiedAt())
                .build();
    }

}
