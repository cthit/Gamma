package it.chalmers.gamma.domain.client.data;

import it.chalmers.gamma.domain.GEntity;
import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.domain.text.Text;

import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "itclient")
public class Client implements GEntity<ClientDTO> {

    @EmbeddedId
    private ClientId clientId;

    @Embedded
    private ClientSecret clientSecret;

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri;

    @Column(name = "auto_approve")
    private boolean autoApprove;

    @Column(name = "name")
    private String name;

    @JoinColumn(name = "description")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Text description;

    public Client() { }

    public Client(ClientDTO client) {
        try {
            apply(client);
        } catch (IDsNotMatchingException ignored) { }
    }

    @Override
    public void apply(ClientDTO c) throws IDsNotMatchingException {
        if(this.clientId != null && !this.clientId.equals(c.getClientId())) {
            throw new IDsNotMatchingException();
        }

        this.clientId = c.getClientId();
        this.clientSecret = c.getClientSecret();
        this.webServerRedirectUri = c.getWebServerRedirectUri();
        this.autoApprove = c.isAutoApprove();
        this.name = c.getName();
        this.description = c.getDescription();

    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }

    public ClientSecret getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(ClientSecret clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
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
        return this.autoApprove == client.autoApprove
            && Objects.equals(this.clientId, client.clientId)
            && Objects.equals(this.clientSecret, client.clientSecret)
            && Objects.equals(this.webServerRedirectUri, client.webServerRedirectUri)
            && Objects.equals(this.name, client.name)
            && Objects.equals(this.description, client.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.clientId,
                this.clientSecret,
                this.webServerRedirectUri,
                this.autoApprove,
                this.name,
                this.description
        );
    }

    @Override
    public String toString() {
        return "Client{"
                + ", clientId='" + this.clientId + '\''
                + ", clientSecret={redacted}'\''"
                + ", webServerRedirectUri='" + this.webServerRedirectUri + '\''
                + ", autoApprove=" + this.autoApprove
                + ", name='" + this.name + '\''
                + ", description='" + this.description + '\''
                + '}';
    }
}
