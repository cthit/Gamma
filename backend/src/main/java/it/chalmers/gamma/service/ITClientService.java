package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.db.repository.ITClientDetailsRepository;

import it.chalmers.gamma.requests.AddITClientRequest;
import it.chalmers.gamma.util.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        ITClient client = this.itClientDetailsRepository.findByClientId(clientId);
        BaseClientDetails details = new BaseClientDetails();
        details.setClientId(client.getClientId());
        details.setAuthorizedGrantTypes(Arrays.asList("authorization_code") );
        details.setClientSecret(passwordEncoder().encode("secret"));
        details.setScope(Arrays.asList("read,write"));
        details.setRegisteredRedirectUri(Collections.singleton("http://localhost:8083/ui/login"));
        details.setResourceIds(Arrays.asList("oauth2-resource"));
        details.setAutoApproveScopes(Arrays.asList("read,write"));
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
        details.setAuthorities(authorities);
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
}
