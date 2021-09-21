package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.domain.client.Client;
import it.chalmers.gamma.domain.client.ClientId;
import it.chalmers.gamma.domain.client.ClientSecret;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;

import javax.persistence.*;
import java.util.Collections;

@Entity
@Table(name = "itclient")
public class ClientEntity extends ImmutableEntity<ClientId> {

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

    public ClientEntity(Client client, ClientSecret clientSecret) {
        this.clientId = client.clientId().getValue();
        this.clientSecret = clientSecret.value();
        this.description = new TextEntity(client.description());
        this.webServerRedirectUri = client.webServerRedirectUri();
        this.autoApprove = client.autoApprove();
        this.prettyName = client.prettyName().value();
        this.description.apply(client.description());
    }

    @Override
    protected ClientId id() {
        return ClientId.valueOf(this.clientId);
    }

    public Client toDomain() {
        return new Client(
                ClientId.valueOf(this.clientId),
                new ClientSecret(this.clientSecret),
                this.webServerRedirectUri,
                this.autoApprove,
                new PrettyName(this.prettyName),
                this.description.toDomain(),
                Collections.EMPTY_LIST, //TODO: JoinColumn
                Collections.EMPTY_LIST,//TODO: JoinColumn
                null //TODO join potential api key
        );
    }
}
