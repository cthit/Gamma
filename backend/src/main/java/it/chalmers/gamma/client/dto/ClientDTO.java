package it.chalmers.gamma.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.chalmers.gamma.domain.text.Text;
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

public class ClientDTO implements ClientDetails {

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

    public ClientDTO(UUID id,
                     String clientId,
                     String clientSecret,
                     String webServerRedirectUri,
                     int accessTokenValidity,
                     int refreshTokenValidity,
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

        ClientDTO itClient = (ClientDTO) o;
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

    public static class ClientDTOBuilder {

        private UUID id;
        private String clientId;
        private String clientSecret;
        private String webServerRedirectUri;
        private int accessTokenValidity;
        private int refreshTokenValidity;
        private boolean autoApprove;
        private String name;
        private Text description;
        private Instant createdAt;
        private Instant lastModifiedAt;

        public ClientDTOBuilder setId(UUID id) {
            this.id = id;
            return this;
        }

        public ClientDTOBuilder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public ClientDTOBuilder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public ClientDTOBuilder setWebServerRedirectUri(String webServerRedirectUri) {
            this.webServerRedirectUri = webServerRedirectUri;
            return this;
        }

        public ClientDTOBuilder setAccessTokenValidity(int accessTokenValidity) {
            this.accessTokenValidity = accessTokenValidity;
            return this;
        }

        public ClientDTOBuilder setRefreshTokenValidity(int refreshTokenValidity) {
            this.refreshTokenValidity = refreshTokenValidity;
            return this;
        }

        public ClientDTOBuilder setAutoApprove(boolean autoApprove) {
            this.autoApprove = autoApprove;
            return this;
        }

        public ClientDTOBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ClientDTOBuilder setDescription(Text description) {
            this.description = description;
            return this;
        }

        public ClientDTOBuilder setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ClientDTOBuilder setLastModifiedAt(Instant lastModifiedAt) {
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public ClientDTO build() {
            return new ClientDTO(id, clientId, clientSecret, webServerRedirectUri, accessTokenValidity, refreshTokenValidity, autoApprove, name, description, createdAt, lastModifiedAt);
        }
    }

}
