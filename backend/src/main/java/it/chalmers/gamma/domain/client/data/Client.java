package it.chalmers.gamma.domain.client.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import it.chalmers.gamma.domain.GEntity;
import it.chalmers.gamma.domain.IDsNotMatchingException;
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
public class Client implements GEntity<ClientDTO> {

    @Id
    @Column(updatable = false)
    private UUID id;

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

    @JoinColumn(name = "description", nullable = false)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    public Client() { }

    public Client(ClientDTO client) {

    }

    @Override
    public void apply(ClientDTO c) throws IDsNotMatchingException {
        if(this.id != c.getId()) {
            throw new IDsNotMatchingException();
        }

        this.clientId = c.getClientId();
        this.clientSecret = c.getClientSecret();
        this.webServerRedirectUri = c.getWebServerRedirectUri();
        this.accessTokenValidity = c.getAccessTokenValidity();
        this.refreshTokenValidity = c.getRefreshTokenValidity();
        this.autoApprove = c.isAutoApprove();
        this.name = c.getName();
        this.description = c.getDescription();

    }

    public UUID getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public int getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public int getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public boolean isAutoApprove() {
        return autoApprove;
    }

    public void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Client client = (Client) o;
        return this.accessTokenValidity == client.accessTokenValidity
            && this.refreshTokenValidity == client.refreshTokenValidity
            && this.autoApprove == client.autoApprove
            && Objects.equals(this.id, client.id)
            && Objects.equals(this.clientId, client.clientId)
            && Objects.equals(this.clientSecret, client.clientSecret)
            && Objects.equals(this.webServerRedirectUri, client.webServerRedirectUri)
            && Objects.equals(this.name, client.name)
            && Objects.equals(this.description, client.description);
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
                this.description
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
                + '}';
    }
}
