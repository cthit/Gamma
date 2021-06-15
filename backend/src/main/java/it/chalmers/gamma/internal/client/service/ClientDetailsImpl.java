package it.chalmers.gamma.internal.client.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.chalmers.gamma.domain.Client;
import it.chalmers.gamma.domain.ClientSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

public class ClientDetailsImpl implements ClientDetails {

    @Value("${application.auth.accessTokenValidityTime}")
    private static int accessTokenValidityTime;

    @Value("${application.auth.refreshTokenValidityTime}")
    private static int refreshTokenValidityTime;

    private final Client client;
    private final ClientSecret clientSecret;

    public ClientDetailsImpl(Client client, ClientSecret clientSecret) {
        this.client = client;
        this.clientSecret = clientSecret;
    }

    @Override
    public String getClientId() {
        return this.client.clientId().get();
    }
    
    @Override
    public boolean isAutoApprove(String scope) {
        return this.client.autoApprove();
    }

    @Override
    @JsonIgnore
    public Set<String> getResourceIds() {
        return null;
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    @JsonIgnore
    public String getClientSecret() {
        return this.clientSecret.get();
    }

    @Override
    public boolean isScoped() {
        return false;
    }

    @Override
    public Set<String> getScope() {
        Set<String> scopes = new HashSet<>();
        scopes.add("access");
        return scopes;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        Set<String> authorized = new HashSet<>();
        authorized.add("authorization_code");
        return authorized;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        Set<String> authorized = new HashSet<>();
        authorized.add(this.client.webServerRedirectUri());
        return authorized;
    }

    @Override
    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
        return authorities;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValidityTime;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValidityTime;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        Map<String, Object> additionalInformation = new HashMap<>();
        additionalInformation.put("name", this.client.prettyName());
        additionalInformation.put("description", this.client.description());
        return additionalInformation;
    }

}
