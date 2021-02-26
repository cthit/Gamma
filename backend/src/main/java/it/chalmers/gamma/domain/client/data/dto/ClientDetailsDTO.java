package it.chalmers.gamma.domain.client.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

public class ClientDetailsDTO implements ClientDetails {

    @Value("${application.auth.accessTokenValidityTime}")
    private static int accessTokenValidityTime;

    @Value("${application.auth.refreshTokenValidityTime}")
    private static int refreshTokenValidityTime;

    private final ClientDTO client;

    public ClientDetailsDTO(ClientDTO client) {
        this.client = client;
    }

    @Override
    public String getClientId() {
        return this.client.getClientId().get();
    }
    
    @Override
    public boolean isAutoApprove(String scope) {
        return this.client.isAutoApprove();
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
        return this.client.getClientSecret().get();
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
        authorized.add(this.client.getWebServerRedirectUri());
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
        additionalInformation.put("name", this.client.getName());
        additionalInformation.put("description", this.client.getDescription());
        return additionalInformation;
    }

}