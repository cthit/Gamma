package it.chalmers.gamma.adapter.secondary.jpa.client;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserApprovalEntity;
import it.chalmers.gamma.app.domain.client.Client;
import it.chalmers.gamma.app.domain.client.ClientId;
import it.chalmers.gamma.app.domain.client.ClientSecret;
import it.chalmers.gamma.adapter.secondary.jpa.util.ImmutableEntity;
import it.chalmers.gamma.app.domain.client.WebServerRedirectUrl;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.adapter.secondary.jpa.text.TextEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "itclient")
public class ClientEntity extends ImmutableEntity<ClientId> {

    @Id
    @Column(name = "client_id")
    protected String clientId;

    @Column(name = "client_secret")
    protected String clientSecret;

    @Column(name = "web_server_redirect_uri")
    protected String webServerRedirectUri;

    @Column(name = "auto_approve")
    protected boolean autoApprove;

    @Column(name = "pretty_name")
    protected String prettyName;

    @JoinColumn(name = "description")
    @OneToOne(cascade = CascadeType.MERGE)
    protected TextEntity description;

    @OneToMany(mappedBy = "id.client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ClientRestrictionEntity> restrictions;

    @OneToMany(mappedBy = "id.client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<UserApprovalEntity> approvals;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    protected ClientApiKeyEntity clientsApiKey;

    protected ClientEntity() {
        //TODO: Will a loading of the entity override this?
        this.description = new TextEntity();
    }

    @Override
    protected ClientId id() {
        return ClientId.valueOf(this.clientId);
    }

    public Client toDomain() {
        return new Client(
                ClientId.valueOf(this.clientId),
                new ClientSecret(this.clientSecret),
                new WebServerRedirectUrl(this.webServerRedirectUri),
                this.autoApprove,
                new PrettyName(this.prettyName),
                this.description.toDomain(),
                new ArrayList<>(), //TODO: JoinColumn
                new ArrayList<>(),//TODO: JoinColumn
                null //TODO join potential api key
        );
    }
}
