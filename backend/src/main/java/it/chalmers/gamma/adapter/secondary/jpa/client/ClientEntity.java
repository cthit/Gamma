package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.app.domain.Client;
import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.ClientSecret;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;

import javax.persistence.*;

@Entity
@Table(name = "itclient")
public class ClientEntity extends ImmutableEntity<ClientId, Client> {

    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri;

    @Column(name = "auto_approve")
    private boolean autoApprove;

    @Column(name = "pretty_name")
    private String prettyName;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.MERGE)
    private TextEntity description;

    protected ClientEntity() { }

    public ClientEntity(Client client) {
        this.clientId = client.clientId().value();
        this.clientSecret = client.clientSecret().value();
        this.description = new TextEntity(client.description());
        this.webServerRedirectUri = client.webServerRedirectUri();
        this.autoApprove = client.autoApprove();
        this.prettyName = client.prettyName().value();
        this.description.apply(client.description());
    }

    protected void setClientSecret(ClientSecret clientSecret) {
        this.clientSecret = clientSecret.value();
    }

    @Override
    protected ClientId id() {
        return ClientId.valueOf(this.clientId);
    }

    @Override
    public Client toDTO() {
        return new Client(
                ClientId.valueOf(this.clientId),
                new ClientSecret(this.clientSecret),
                this.webServerRedirectUri,
                this.autoApprove,
                new PrettyName(this.prettyName),
                this.description.toDTO()
        );
    }
}
