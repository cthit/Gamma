package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.ITClientDetailsRepository;

import it.chalmers.gamma.requests.AddITClientRequest;
import it.chalmers.gamma.util.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ITClientService implements ClientDetailsService {

    @Value("${application.auth.accessTokenValidityTime}")       // TODO Fix this
    private int accessTokenValidityTime;

    @Value("${application.auth.autoApprove}")
    private boolean autoApprove;

    @Value("${application.auth.refreshTokenValidityTime}")
    private int refreshTokenValidityTime;

    private ITClientDetailsRepository itClientDetailsRepository;

    public ITClientService(ITClientDetailsRepository itClientDetailsRepository){
        this.itClientDetailsRepository = itClientDetailsRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return this.itClientDetailsRepository.findByClientId(clientId);
    }

    public void createITClient(AddITClientRequest request) {
        ITClient client = new ITClient();
        client.setName(request.getName());
        Text description = new Text();
        description.setEn(request.getDescription().getEn());
        description.setSv(request.getDescription().getSv());
        System.out.println(description);
        client.setDescription(description);
        client.setWebServerRedirectUri(request.getUrlRedirect());
        client.setCreatedAt(Instant.now());
        client.setLastModifiedAt(Instant.now());
        client.setAccessTokenValidity(accessTokenValidityTime);
        client.setAutoApprove(autoApprove);
        client.setRefreshTokenValidity(refreshTokenValidityTime);
        client.setClientId(TokenUtils.generateToken());
        client.setClientSecret(TokenUtils.generateToken());
        itClientDetailsRepository.save(client);
    }
}
