package it.chalmers.gamma.app.client.service;

import it.chalmers.gamma.app.domain.Client;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.ClientSecret;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.util.entity.ImmutableEntity;
import it.chalmers.gamma.app.service.TextEntity;

import javax.persistence.*;

@Entity
@Table(name = "itclient")
public class ClientEntity extends ImmutableEntity<ClientId, Client> {

    @EmbeddedId
    private ClientId clientId;

    @Embedded
    private ClientSecret clientSecret;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity description;

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri;

    @Column(name = "auto_approve")
    private boolean autoApprove;

    @Embedded
    private PrettyName prettyName;

    protected ClientEntity() { }

    protected ClientEntity(Client client, ClientSecret clientSecret) {
        this.clientId = client.clientId();
        this.clientSecret = clientSecret;
        this.description = new TextEntity(client.description());
        this.webServerRedirectUri = client.webServerRedirectUri();
        this.autoApprove = client.autoApprove();
        this.prettyName = client.prettyName();
        this.description.apply(client.description());
    }

    protected ClientSecret getClientSecret() {
        return this.clientSecret;
    }

    protected void setClientSecret(ClientSecret clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    protected ClientId id() {
        return this.clientId;
    }

    @Override
    protected Client toDTO() {
        return new Client(
                this.clientId,
                this.webServerRedirectUri,
                this.autoApprove,
                this.prettyName,
                this.description.toDTO()
        );
    }
}
