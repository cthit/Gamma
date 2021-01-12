package it.chalmers.gamma.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import it.chalmers.gamma.domain.text.Text;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "itclient")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ITClient {

    @Id
    @Column(updatable = false)
    private final UUID id;

    @Column(name = "client_id", length = 256, nullable = false)
    private String clientId;

    @Column(name = "client_secret", length = 256, nullable = false)
    @JsonIgnore
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

    @SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "is serializable by Jackson")
    @JoinColumn(name = "description", nullable = false)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private Instant lastModifiedAt;

    public ITClient() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return this.id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getWebServerRedirectUri() {
        return this.webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public int getAccessTokenValidity() {
        return this.accessTokenValidity;
    }

    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public int getRefreshTokenValidity() {
        return this.refreshTokenValidity;
    }

    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return this.description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public ITClientDTO toDTO() {
        return new ITClientDTO(
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ITClient itClient = (ITClient) o;
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
