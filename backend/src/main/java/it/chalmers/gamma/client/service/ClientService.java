package it.chalmers.gamma.client.service;

import it.chalmers.gamma.client.data.Client;
import it.chalmers.gamma.client.data.ClientRepository;
import it.chalmers.gamma.client.dto.ClientDTO;
import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.client.controller.response.ClientDoesNotExistException;
import it.chalmers.gamma.util.TokenUtils;

import it.chalmers.gamma.util.UUIDUtil;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
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

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        return this.clientRepository.findByClientId(clientId)
                .orElseThrow(ClientDoesNotExistException::new);
    }

    public ClientDTO createITClient(String name, Text description, String redirect, boolean autoApprove) {
        Client client = new Client();
        client.setName(name);
        client.setDescription(description == null ? new Text() : description);
        client.setWebServerRedirectUri(redirect);
        client.setCreatedAt(Instant.now());
        client.setLastModifiedAt(Instant.now());
        client.setAccessTokenValidity(this.accessTokenValidityTime);
        client.setAutoApprove(autoApprove);
        client.setRefreshTokenValidity(this.refreshTokenValidityTime);
        client.setClientId(TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS)
        );
        String clientSecret = TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS);
        client.setClientSecret("{noop}" + clientSecret);
        return this.clientRepository.save(client).toDTO();
    }

    public List<ClientDTO> getAllClients() {
        return this.clientRepository.findAll().stream().map(Client::toDTO).collect(Collectors.toList());
    }


    public void removeITClient(UUID id) {
        this.clientRepository.deleteById(id);
    }

    public void editClient(UUID id, ClientDTO clientDTO) {
        Client client = this.clientRepository.findById(id).orElseThrow();
        client.setLastModifiedAt(Instant.now());
        client.setName(clientDTO.getName() == null ? client.getName() : clientDTO.getName());
        client.setDescription(clientDTO.getDescription() == null
                ? client.getDescription() : clientDTO.getDescription());
        client.setWebServerRedirectUri(clientDTO.getWebServerRedirectUri() == null
                ? client.getWebServerRedirectUri() : clientDTO.getWebServerRedirectUri());
    }

    public boolean clientExists(String id) {
        return UUIDUtil.validUUID(id) && this.clientRepository.existsById(UUID.fromString(id))
            || this.clientRepository.existsITClientByClientId(id);
    }


    public void addITClient(Client client) {
        this.clientRepository.save(client);
    }

}
