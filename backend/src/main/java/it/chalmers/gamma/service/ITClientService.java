package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.ITClientRepository;
import it.chalmers.gamma.domain.dto.access.ITClientDTO;
import it.chalmers.gamma.response.client.ITClientDoesNotExistException;
import it.chalmers.gamma.util.TokenUtils;

import it.chalmers.gamma.util.UUIDUtil;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
public class ITClientService implements ClientDetailsService {

    @Value("${application.auth.accessTokenValidityTime}")       // TODO Fix this
    private int accessTokenValidityTime;

    @Value("${application.auth.autoApprove}")
    private boolean autoApprove;

    @Value("${application.auth.refreshTokenValidityTime}")
    private int refreshTokenValidityTime;

    private final ITClientRepository itClientRepository;

    public ITClientService(ITClientRepository itClientRepository) {
        this.itClientRepository = itClientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return this.itClientRepository.findByClientId(clientId).orElseThrow(ITClientDoesNotExistException::new)
        .toDTO();
    }

    public ITClientDTO createITClient(String name, Text description, String redirect) {
        ITClient client = new ITClient();
        client.setName(name);
        client.setDescription(description == null ? new Text() : description);
        client.setWebServerRedirectUri(redirect);
        client.setCreatedAt(Instant.now());
        client.setLastModifiedAt(Instant.now());
        client.setAccessTokenValidity(this.accessTokenValidityTime);
        client.setAutoApprove(this.autoApprove);
        client.setRefreshTokenValidity(this.refreshTokenValidityTime);
        client.setClientId(TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS));
        String clientSecret = TokenUtils.generateToken(75, TokenUtils.CharacterTypes.LOWERCASE,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS);
        client.setClientSecret("{noop}" + clientSecret);
        return this.itClientRepository.save(client).toDTO();
    }

    public List<ITClientDTO> getAllClients() {
        return this.itClientRepository.findAll().stream().map(ITClient::toDTO).collect(Collectors.toList());
    }

    public ITClientDTO getITClient(UUID id) {
        return this.itClientRepository.findById(id).map(ITClient::toDTO).orElseThrow();
    }

    protected ITClient getITClient(ITClientDTO clientDTO) {
        return this.itClientRepository.findById(clientDTO.getId()).orElse(null);
    }

    public void removeITClient(UUID id) {
        this.itClientRepository.deleteById(id);
    }

    public void editClient(UUID id, ITClientDTO clientDTO) {
        ITClient client = this.itClientRepository.findById(id).orElseThrow();
        client.setLastModifiedAt(Instant.now());
        client.setName(clientDTO.getName() == null ? client.getName() : clientDTO.getName());
        client.setDescription(clientDTO.getDescription() == null
                ? client.getDescription() : clientDTO.getDescription());
        client.setWebServerRedirectUri(clientDTO.getWebServerRedirectUri() == null
                ? client.getWebServerRedirectUri() : clientDTO.getWebServerRedirectUri());
    }

    public boolean clientExists(String id) {
        return UUIDUtil.validUUID(id) && this.itClientRepository.existsById(UUID.fromString(id))
                || this.itClientRepository.existsITClientByClientId(id);
    }


    public void addITClient(ITClient itClient) {
        this.itClientRepository.save(itClient);
    }

}
