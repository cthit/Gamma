package it.chalmers.delta.service;

import it.chalmers.delta.db.entity.ITClient;
import it.chalmers.delta.db.entity.Text;
import it.chalmers.delta.db.repository.ITClientRepository;
import it.chalmers.delta.requests.AddITClientRequest;
import it.chalmers.delta.util.TokenUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
        return this.itClientRepository.findByClientId(clientId);
    }

    public String createITClient(AddITClientRequest request) {
        ITClient client = new ITClient();
        client.setName(request.getName());
        Text description = new Text();
        description.setEn(request.getDescription().getEn());
        description.setSv(request.getDescription().getSv());
        client.setDescription(description);
        client.setWebServerRedirectUri(request.getWebServerRedirectUri());
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
        this.itClientRepository.save(client);
        return clientSecret;
    }

    public List<ITClient> getAllClients() {
        return this.itClientRepository.findAll();
    }

    public ITClient getITClient(UUID id) {
        return this.itClientRepository.findById(id).orElseThrow();
    }

    public void removeITClient(UUID id) {
        this.itClientRepository.deleteById(id);
    }

    public void editClient(UUID id, AddITClientRequest request) {
        ITClient client = this.itClientRepository.findById(id).orElseThrow();
        client.setLastModifiedAt(Instant.now());
        client.setName(request.getName() == null ? client.getName() : request.getName());
        client.setDescription(request.getDescription() == null ? client.getDescription() : request.getDescription());
        client.setWebServerRedirectUri(request.getWebServerRedirectUri() == null
                ? client.getWebServerRedirectUri() : request.getWebServerRedirectUri());
    }

    public boolean clientExists(UUID id) {
        return this.itClientRepository.existsById(id);
    }

    public boolean clientExistsByClientId(String clientId) {
        return this.itClientRepository.existsITClientByClientId(clientId);
    }

    public void addITClient(ITClient itClient) {
        this.itClientRepository.save(itClient);
    }
}
