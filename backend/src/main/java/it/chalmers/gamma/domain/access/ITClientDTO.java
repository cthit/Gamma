package it.chalmers.gamma.domain.access;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.chalmers.gamma.db.entity.Text;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

@SuppressWarnings("PMD.ExcessiveParameterList")
public class ITClientDTO implements ClientDetails {
    private final UUID id;
    private final String clientId;
    @JsonIgnore
    private final String clientSecret;
    private final String webServerRedirectUri;
    @JsonIgnore
    private final int accessTokenValidity;
    @JsonIgnore
    private final int refreshTokenValidity;
    private final boolean autoApprove;
    private final String name;
    private final Text description;
    @JsonIgnore
    private final Instant createdAt;
    @JsonIgnore
    private final Instant lastModifiedAt;


    public ITClientDTO(UUID id,
                       String clientId,
                       String clientSecret,
                       String webServerRedirectUri,
                       Integer accessTokenValidity,
                       Integer refreshTokenValidity,
                       boolean autoApprove,
                       String name,
                       Text description,
                       Instant createdAt,
                       Instant lastModifiedAt) {
        this.id = id;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webServerRedirectUri = webServerRedirectUri;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.autoApprove = autoApprove;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    public ITClientDTO(String webServerRedirectUri, String name, Text description, boolean autoApprove) {
        this(null, null, null, webServerRedirectUri, Integer.MAX_VALUE, Integer.MAX_VALUE,
                autoApprove, name, description, null, null);
    }

    public UUID getId() {
        return this.id;
    }

    public String getWebServerRedirectUri() {
        return this.webServerRedirectUri;
    }

    public int getAccessTokenValidity() {
        return this.accessTokenValidity;
    }

    public int getRefreshTokenValidity() {
        return this.refreshTokenValidity;
    }

    public String getName() {
        return this.name;
    }

    public Text getDescription() {
        return this.description;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    public boolean isAutoApprove() {
        return this.autoApprove;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return isAutoApprove();
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
        return this.clientSecret;
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
        authorized.add(this.webServerRedirectUri);
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
        return this.accessTokenValidity;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return this.refreshTokenValidity;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        Map<String, Object> additionalInformation = new HashMap<>();
        additionalInformation.put("name", this.name);
        additionalInformation.put("description", this.description);

        return additionalInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ITClientDTO itClient = (ITClientDTO) o;
        return this.accessTokenValidity == itClient.accessTokenValidity
                && this.refreshTokenValidity == itClient.refreshTokenValidity
                && this.autoApprove == itClient.autoApprove
                && Objects.equals(this.id, itClient.id)
                && Objects.equals(this.clientId, itClient.clientId)
                && Objects.equals(this.clientSecret, itClient.clientSecret)
                && Objects.equals(this.webServerRedirectUri, itClient.webServerRedirectUri)
                && Objects.equals(this.name, itClient.name)
                && Objects.equals(this.description, itClient.description)
                && Objects.equals(this.createdAt, itClient.createdAt)
                && Objects.equals(this.lastModifiedAt, itClient.lastModifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.clientId,
                this.clientSecret,
                this.webServerRedirectUri,
                this.accessTokenValidity,
                this.refreshTokenValidity,
                this.autoApprove,
                this.name,
                this.description,
                this.createdAt,
                this.lastModifiedAt
        );
    }

    @Override
    public String toString() {
        return "ITClient{"
                + "id=" + this.id
                + ", clientId='" + this.clientId + '\''
                + ", clientSecret={redacted}'\''"
                + ", webServerRedirectUri='" + this.webServerRedirectUri + '\''
                + ", accessTokenValidity=" + this.accessTokenValidity
                + ", refreshTokenValidity=" + this.refreshTokenValidity
                + ", autoApprove=" + this.autoApprove
                + ", name='" + this.name + '\''
                + ", description='" + this.description + '\''
                + ", createdAt=" + this.createdAt
                + ", lastModifiedAt=" + this.lastModifiedAt
                + '}';
    }
}
