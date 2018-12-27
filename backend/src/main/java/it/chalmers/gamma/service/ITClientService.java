package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.ITClientDetailsRepository;
import it.chalmers.gamma.requests.AddITClientRequest;
import it.chalmers.gamma.util.TokenUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

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

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        ITClient client = this.itClientDetailsRepository.findByClientId(clientId);
        BaseClientDetails details = new BaseClientDetails();
        details.setClientId(client.getClientId());
        details.setAuthorizedGrantTypes(client.getAuthorizedGrantTypes());
        details.setClientSecret(client.getClientSecret()); //this used passwordEncoder before, why?
        details.setScope(client.getScope());
        details.setRegisteredRedirectUri(client.getRegisteredRedirectUri());
        details.setAutoApproveScopes(client.getScope());        // Change this to force a approval from user.
        details.setAuthorities(client.getAuthorities());
        return details;
    }
    public void createITClient(AddITClientRequest request) {
        ITClient client = new ITClient();
        client.setName(request.getName());
        Text description = new Text();
        description.setEn(request.getDescription().getEn());
        description.setSv(request.getDescription().getSv());
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

    public List<ITClient> getAllClients(){
        return itClientDetailsRepository.findAll();
    }

    public ITClient getITClient(UUID id){
        return itClientDetailsRepository.findById(id).orElseThrow();
    }

    public void removeITClient(UUID id){
        itClientDetailsRepository.deleteById(id);
    }

    public void editClient(UUID id, AddITClientRequest request){
        ITClient client = itClientDetailsRepository.findById(id).orElseThrow();
        client.setLastModifiedAt(Instant.now());
        client.setName(request.getName() == null ? client.getName() : request.getName());
        client.setDescription(request.getDescription() == null ? client.getDescription() : request.getDescription());
        client.setWebServerRedirectUri(request.getUrlRedirect() == null
                ? client.getWebServerRedirectUri() : request.getUrlRedirect());
    }

    public boolean clientExists(UUID id){
        return itClientDetailsRepository.existsById(id);
    }
}
