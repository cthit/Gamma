package it.chalmers.gamma.db.entity;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

@Entity
@Table(name = "itclient")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ITClient implements ClientDetails {

    @Id
    @Column(updatable = false)
    private UUID id;

    @Column(name = "client_id", length = 256, nullable = false)
    private String clientId;

    @Column(name = "client_secret", length = 256, nullable = false)
    private String clientSecret;

    @Column(name = "web_server_redirect_uri", length = 256, nullable = false)
    private String webServerRedirectUri;

    @Column(name = "access_token_validity", nullable = false)
    private int accessTokenValidity;

    @Column(name = "refresh_token_validity", nullable = false)
    private int refreshTokenValidity;

    @Column(name = "auto_approve", nullable = false)
    private boolean autoApprove;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 512, nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return null;
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return false;
    }

    @Override
    public Set<String> getScope() {
        return null;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return null;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return null;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValidity;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValidity;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return autoApprove;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return new HashMap<>(){{
            put("name", name);
            put("description", description);
        }};
    }
}
